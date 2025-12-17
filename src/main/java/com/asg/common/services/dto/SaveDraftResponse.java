package com.asg.common.services.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveDraftResponse {
    private String docId;
    private Long companyPoid;
    private Long userId;
}
