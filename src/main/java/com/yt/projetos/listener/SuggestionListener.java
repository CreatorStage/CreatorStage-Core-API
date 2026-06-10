package com.yt.projetos.listener;

import com.yt.projetos.config.RabbitMQConfig;
import com.yt.projetos.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuggestionListener {

    private final SuggestionService suggestionService;

    @RabbitListener(queues = RabbitMQConfig.SCRAPE_RESULTS_QUEUE)
    public void handleScrapeResults(Map<String, Object> message) {
        log.info("Mensagem de resultado de scraping recebida do RabbitMQ: {}", message);

        try {
            String status = (String) message.get("status");
            if (!"success".equals(status)) {
                log.error("Erro reportado pelo Scraper para o canal: {}. Mensagem: {}", 
                          message.get("sourceChannelUrl"), message.get("error"));
                return;
            }

            String channelIdStr = (String) message.get("channelId");
            UUID channelId = UUID.fromString(channelIdStr);
            String sourceChannelUrl = (String) message.get("sourceChannelUrl");
            String sourceChannelName = (String) message.get("sourceChannelName");
            List<Map<String, String>> videos = (List<Map<String, String>>) message.get("videos");

            if (videos != null) {
                suggestionService.saveSuggestions(channelId, sourceChannelUrl, sourceChannelName, videos);
            } else {
                log.warn("Nenhum vídeo retornado no resultado de scraping para {}", sourceChannelUrl);
            }
        } catch (Exception e) {
            log.error("Erro ao processar mensagem de resultado de scraping: ", e);
        }
    }
}
