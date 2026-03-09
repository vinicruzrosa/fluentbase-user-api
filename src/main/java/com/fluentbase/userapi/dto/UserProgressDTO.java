package com.fluentbase.userapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressDTO {

    private UUID id;
    private UUID userId;
    private int xp;
    private int streak;
    private LocalDate lastActivityDate;
    private String proficiencyLevel;
    private Map<String, Object> difficultyAreas;
}
