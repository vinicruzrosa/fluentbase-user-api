package com.fluentbase.userapi.service;

import com.fluentbase.userapi.dto.*;
import com.fluentbase.userapi.entity.User;
import com.fluentbase.userapi.entity.UserProgress;
import com.fluentbase.userapi.exception.ResourceNotFoundException;
import com.fluentbase.userapi.exception.UnauthorizedException;
import com.fluentbase.userapi.repository.UserProgressRepository;
import com.fluentbase.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;

    public UserProgressDTO getProgress(UUID userId) {
        authorizeAccess(userId);
        UserProgress progress = findProgressOrThrow(userId);
        return toDTO(progress);
    }

    @Transactional
    public UserProgressDTO addXp(UUID userId, XpUpdateRequest request) {
        authorizeAccess(userId);
        UserProgress progress = findProgressOrThrow(userId);

        progress.setXp(progress.getXp() + request.getAmount());
        progress.setProficiencyLevel(calculateProficiency(progress.getXp()));

        progress = userProgressRepository.save(progress);
        return toDTO(progress);
    }

    @Transactional
    public UserProgressDTO updateStreak(UUID userId, StreakUpdateRequest request) {
        authorizeAccess(userId);
        UserProgress progress = findProgressOrThrow(userId);

        LocalDate sessionDate = request.getSessionDate();
        LocalDate lastActivity = progress.getLastActivityDate();

        if (lastActivity == null) {
            progress.setStreak(1);
        } else if (sessionDate.equals(lastActivity)) {
            // Same day — no change
        } else if (sessionDate.equals(lastActivity.plusDays(1))) {
            progress.setStreak(progress.getStreak() + 1);
        } else {
            progress.setStreak(1);
        }

        progress.setLastActivityDate(sessionDate);
        progress = userProgressRepository.save(progress);
        return toDTO(progress);
    }

    @Transactional
    public UserProgressDTO updateDifficultyAreas(UUID userId, DifficultyAreasRequest request) {
        authorizeAccess(userId);
        UserProgress progress = findProgressOrThrow(userId);

        progress.setDifficultyAreas(request.getDifficultyAreas());
        progress = userProgressRepository.save(progress);
        return toDTO(progress);
    }

    public UserSummaryDTO getUserSummary(UUID userId) {
        authorizeAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProgress progress = findProgressOrThrow(userId);

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return UserSummaryDTO.builder()
                .user(userDTO)
                .progress(toDTO(progress))
                .build();
    }

    private String calculateProficiency(int xp) {
        if (xp > 2000) return "EXPERT";
        if (xp > 500) return "ADVANCED";
        if (xp > 100) return "INTERMEDIATE";
        return "BEGINNER";
    }

    private UserProgress findProgressOrThrow(UUID userId) {
        return userProgressRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found for user: " + userId));
    }

    private void authorizeAccess(UUID resourceUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User currentUser)) {
            throw new UnauthorizedException("Authentication required");
        }
        if (!currentUser.getId().equals(resourceUserId)) {
            throw new UnauthorizedException("You can only access your own resources");
        }
    }

    private UserProgressDTO toDTO(UserProgress progress) {
        return UserProgressDTO.builder()
                .id(progress.getId())
                .userId(progress.getUser().getId())
                .xp(progress.getXp())
                .streak(progress.getStreak())
                .lastActivityDate(progress.getLastActivityDate())
                .proficiencyLevel(progress.getProficiencyLevel())
                .difficultyAreas(progress.getDifficultyAreas())
                .build();
    }
}
