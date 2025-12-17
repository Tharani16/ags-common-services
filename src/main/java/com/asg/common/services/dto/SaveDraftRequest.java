package com.asg.common.services.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveDraftRequest {
    @NotBlank(message = "Doc Id is required")
    private String docId;

    @NotNull(message = "Company Poid is required")
    private Long companyPoid;

    @NotNull(message = "User Id is required")
    private Long userPoid;

    @NotNull(message = "Json Data is required")
    private JsonNode jsonData;
}