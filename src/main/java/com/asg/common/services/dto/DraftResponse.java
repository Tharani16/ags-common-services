package com.asg.common.services.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DraftResponse {
    private String docId;
    private Long companyPoid;
    private Long userPoid;
    private JsonNode jsonData;
    private String lastModifiedDate;
}
