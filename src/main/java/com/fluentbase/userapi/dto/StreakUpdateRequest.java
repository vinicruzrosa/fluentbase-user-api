package com.fluentbase.userapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreakUpdateRequest {

    @NotNull(message = "Session date is required")
    private LocalDate sessionDate;
}
