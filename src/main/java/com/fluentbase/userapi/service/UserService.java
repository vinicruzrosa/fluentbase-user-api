package com.fluentbase.userapi.service;

import com.fluentbase.userapi.dto.UpdateUserRequest;
import com.fluentbase.userapi.dto.UserDTO;
import com.fluentbase.userapi.entity.User;
import com.fluentbase.userapi.exception.ResourceNotFoundException;
import com.fluentbase.userapi.exception.UnauthorizedException;
import com.fluentbase.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RabbitMQEventPublisher eventPublisher;

    public UserDTO getUserById(UUID id) {
        authorizeAccess(id);
        User user = findUserOrThrow(id);
        return toDTO(user);
    }

    @Transactional
    public UserDTO updateUser(UUID id, UpdateUserRequest request) {
        authorizeAccess(id);
        User user = findUserOrThrow(id);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        user = userRepository.save(user);
        return toDTO(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        authorizeAccess(id);
        User user = findUserOrThrow(id);
        String email = user.getEmail();

        userRepository.delete(user);
        eventPublisher.publishUserDeletedEvent(id, email);
    }

    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
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

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
