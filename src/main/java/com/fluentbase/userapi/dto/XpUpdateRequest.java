package com.fluentbase.userapi.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XpUpdateRequest {

    @Min(value = 1, message = "XP amount must be at least 1")
    private int amount;
}
