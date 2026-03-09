package com.fluentbase.userapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifficultyAreasRequest {

    @NotNull(message = "Difficulty areas data is required")
    private Map<String, Object> difficultyAreas;
}
