package com.yt.projetos.controller;

import com.yt.projetos.model.Goal;
import com.yt.projetos.model.User;
import com.yt.projetos.dto.GoalResponse;
import com.yt.projetos.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    @GetMapping("/channels/{channelId}/goals")
    public List<GoalResponse> getGoals(@AuthenticationPrincipal User currentUser, @PathVariable UUID channelId) {
        return goalService.getGoals(currentUser, channelId).stream().map(this::toGoalResponse).toList();
    }

    @PostMapping("/channels/{channelId}/goals")
    public ResponseEntity<GoalResponse> createGoal(@AuthenticationPrincipal User currentUser, @PathVariable UUID channelId, @RequestBody Goal goal) {
        return ResponseEntity.status(201).body(toGoalResponse(goalService.createGoal(currentUser, channelId, goal)));
    }

    @PutMapping("/goals/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@AuthenticationPrincipal User currentUser, @PathVariable UUID id, @RequestBody Goal updates) {
        return ResponseEntity.ok(toGoalResponse(goalService.updateGoal(currentUser, id, updates)));
    }

    @DeleteMapping("/goals/{id}")
    public ResponseEntity<?> deleteGoal(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
        goalService.deleteGoal(currentUser, id);
        return ResponseEntity.ok().build();
    }

    private GoalResponse toGoalResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getChannel() != null ? goal.getChannel().getId() : null,
                goal.getTitle(),
                goal.getDescription(),
                goal.getTargetValue(),
                goal.getCurrentValue(),
                goal.getDeadline(),
                goal.isCompleted(),
                goal.getCreatedAt()
        );
    }
}
