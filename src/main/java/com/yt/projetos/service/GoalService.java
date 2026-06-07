package com.yt.projetos.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.yt.projetos.model.Channel;
import com.yt.projetos.model.Goal;
import com.yt.projetos.model.User;
import com.yt.projetos.repository.ChannelRepository;
import com.yt.projetos.repository.GoalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final ChannelRepository channelRepository;

    public List<Goal> getGoals(User currentUser, UUID channelId) {
        if (!isOwnedChannel(currentUser, channelId)) {
            return List.of();
        }
        return goalRepository.findByChannelId(channelId);
    }

    public Goal createGoal(User currentUser, UUID channelId, Goal goal) {
        Channel channel = getOwnedChannel(currentUser, channelId);
        goal.setChannel(channel);
        return goalRepository.save(goal);
    }

    public Goal updateGoal(User currentUser, UUID id, Goal updates) {
        Goal goal = goalRepository.findById(id)
                .filter(found -> isOwnedChannel(currentUser, found.getChannel() != null ? found.getChannel().getId() : null))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (updates.getTitle() != null) goal.setTitle(updates.getTitle());
        if (updates.getDescription() != null) goal.setDescription(updates.getDescription());
        if (updates.getTargetValue() != null) goal.setTargetValue(updates.getTargetValue());
        if (updates.getCurrentValue() != null) goal.setCurrentValue(updates.getCurrentValue());
        if (updates.getDeadline() != null) goal.setDeadline(updates.getDeadline());
        goal.setCompleted(updates.isCompleted());
        return goalRepository.save(goal);
    }

    public void deleteGoal(User currentUser, UUID id) {
        Goal goal = goalRepository.findById(id)
                .filter(found -> isOwnedChannel(currentUser, found.getChannel() != null ? found.getChannel().getId() : null))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        goalRepository.delete(goal);
    }

    private Channel getOwnedChannel(User currentUser, UUID channelId) {
        if (currentUser == null || channelId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return channelRepository.findByIdAndUserId(channelId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private boolean isOwnedChannel(User currentUser, UUID channelId) {
        if (currentUser == null || channelId == null) {
            return false;
        }
        return channelRepository.existsByIdAndUserId(channelId, currentUser.getId());
    }
}