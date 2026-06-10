package com.yt.projetos.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.yt.projetos.model.Channel;
import com.yt.projetos.model.Goal;
import com.yt.projetos.dto.GoalRequest;
import com.yt.projetos.model.User;
import com.yt.projetos.repository.ChannelRepository;
import com.yt.projetos.repository.GoalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {

    private final GoalRepository goalRepository;
    private final ChannelRepository channelRepository;

    public List<Goal> getGoals(User currentUser, UUID channelId) {
        if (!isOwnedChannel(currentUser, channelId)) {
            return List.of();
        }
        return goalRepository.findByChannelId(channelId);
    }

    @Transactional
    public Goal createGoal(User currentUser, UUID channelId, GoalRequest request) {
        Channel channel = getOwnedChannel(currentUser, channelId);
        Goal goal = Goal.builder()
                .title(request.title())
                .description(request.description())
                .targetValue(request.targetValue())
                .currentValue(request.currentValue() != null ? request.currentValue() : 0.0)
                .deadline(request.deadline())
                .completed(request.completed())
                .channel(channel)
                .build();
        return goalRepository.save(goal);
    }

    @Transactional
    public Goal updateGoal(User currentUser, UUID id, GoalRequest updates) {
        Goal goal = goalRepository.findById(id)
                .filter(found -> isOwnedChannel(currentUser, found.getChannel() != null ? found.getChannel().getId() : null))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (updates.title() != null) goal.setTitle(updates.title());
        if (updates.description() != null) goal.setDescription(updates.description());
        if (updates.targetValue() != null) goal.setTargetValue(updates.targetValue());
        if (updates.currentValue() != null) goal.setCurrentValue(updates.currentValue());
        if (updates.deadline() != null) goal.setDeadline(updates.deadline());
        goal.setCompleted(updates.completed());
        return goalRepository.save(goal);
    }

    @Transactional
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