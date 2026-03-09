package com.fluentbase.userapi.controller;

import com.fluentbase.userapi.dto.*;
import com.fluentbase.userapi.service.UserProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/{id}")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService userProgressService;

    @GetMapping("/progress")
    public ResponseEntity<UserProgressDTO> getProgress(@PathVariable UUID id) {
        return ResponseEntity.ok(userProgressService.getProgress(id));
    }

    @PutMapping("/progress/xp")
    public ResponseEntity<UserProgressDTO> addXp(
            @PathVariable UUID id,
            @Valid @RequestBody XpUpdateRequest request) {
        return ResponseEntity.ok(userProgressService.addXp(id, request));
    }

    @PutMapping("/progress/streak")
    public ResponseEntity<UserProgressDTO> updateStreak(
            @PathVariable UUID id,
            @Valid @RequestBody StreakUpdateRequest request) {
        return ResponseEntity.ok(userProgressService.updateStreak(id, request));
    }

    @PutMapping("/difficulty-areas")
    public ResponseEntity<UserProgressDTO> updateDifficultyAreas(
            @PathVariable UUID id,
            @Valid @RequestBody DifficultyAreasRequest request) {
        return ResponseEntity.ok(userProgressService.updateDifficultyAreas(id, request));
    }

    @GetMapping("/summary")
    public ResponseEntity<UserSummaryDTO> getUserSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(userProgressService.getUserSummary(id));
    }
}
