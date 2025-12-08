package com.asg.common.services.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserPreferenceRequest(
        @NotNull(message = "Preferences list cannot be null")
        @NotEmpty(message = "Preferences list cannot be empty")
        @Valid
        List<UserPreferenceItem> preferences
) {
    public record UserPreferenceItem(
            @NotNull(message = "Settings name cannot be null")
            @NotEmpty(message = "Settings name cannot be empty")
            String settingsName,
            
            String settingsValue
    ) {}
}