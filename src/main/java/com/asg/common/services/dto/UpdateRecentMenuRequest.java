package com.asg.common.services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateRecentMenuRequest(
        @NotBlank String documentId,
        @NotNull Boolean isDocument
) {}