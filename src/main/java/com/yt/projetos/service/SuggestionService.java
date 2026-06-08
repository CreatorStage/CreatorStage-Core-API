package com.yt.projetos.service;

import com.yt.projetos.model.Channel;
import com.yt.projetos.model.SuggestedVideo;
import com.yt.projetos.repository.SuggestedVideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuggestionService {

    private final SuggestedVideoRepository suggestedVideoRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void scrapeSuggestionsForChannel(Channel channel, String sourceChannelUrl, String sourceChannelName) {
        log.info("Iniciando scraping em background para URL: {}", sourceChannelUrl);

        // Verifica se já não existem sugestões deste canal (evita duplicidade)
        if (suggestedVideoRepository.existsBySourceChannelUrlAndChannelId(sourceChannelUrl, channel.getId())) {
            log.info("Já existem sugestões do canal {} para o channel_id {}. Pulando scraping.", sourceChannelUrl, channel.getId());
            return;
        }

        try {
            String pythonApiUrl = System.getenv().getOrDefault("SCRAPER_API_URL", "http://localhost:8000/scrape");
            Map<String, Object> request = Map.of("urls", List.of(sourceChannelUrl));

            Map<String, Object> response = restTemplate.postForObject(pythonApiUrl, request, Map.class);
            if (response != null && "success".equals(response.get("status"))) {
                List<Map<String, String>> data = (List<Map<String, String>>) response.get("data");

                for (Map<String, String> videoData : data) {
                    String title = videoData.get("titulo");
                    String url = videoData.get("url_video");
                    String views = videoData.get("visualizacoes");
                    
                    // Tentar extrair o ID do vídeo para montar a thumbnail
                    String thumbnailUrl = extractThumbnailUrl(url);

                    SuggestedVideo suggestedVideo = SuggestedVideo.builder()
                            .channel(channel)
                            .sourceChannelName(sourceChannelName != null ? sourceChannelName : videoData.get("canal"))
                            .sourceChannelUrl(sourceChannelUrl)
                            .title(title)
                            .url(url)
                            .views(views)
                            .thumbnailUrl(thumbnailUrl)
                            .build();

                    suggestedVideoRepository.save(suggestedVideo);
                }
                log.info("Scraping finalizado. {} vídeos salvos como sugestões.", data.size());
            } else {
                log.error("Falha na API Python: {}", response);
            }
        } catch (Exception e) {
            log.error("Erro ao chamar serviço de scraping Python: ", e);
        }
    }

    public List<SuggestedVideo> getSuggestionsForChannel(java.util.UUID channelId) {
        return suggestedVideoRepository.findByChannelIdOrderByCreatedAtDesc(channelId);
    }

    public boolean isAlreadyScraped(String sourceChannelUrl, java.util.UUID channelId) {
        return suggestedVideoRepository.existsBySourceChannelUrlAndChannelId(sourceChannelUrl, channelId);
    }

    public void deleteSuggestion(java.util.UUID videoId) {
        suggestedVideoRepository.deleteById(videoId);
    }
    
    private String extractThumbnailUrl(String videoUrl) {
        if (videoUrl == null) return null;
        Pattern pattern = Pattern.compile("(?:youtube\\.com\\/(?:[^/]+\\/.+\\/|(?:v|e(?:mbed)?)\\/|.*[?&]v=)|youtu\\.be\\/)([^\"&?/ ]{11})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);
        if (matcher.find()) {
            return "https://img.youtube.com/vi/" + matcher.group(1) + "/hqdefault.jpg";
        }
        return null;
    }
}
