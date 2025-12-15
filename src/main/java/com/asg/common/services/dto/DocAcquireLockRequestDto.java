package com.asg.common.services.dto;

import lombok.Data;

@Data
public class DocAcquireLockRequestDto {
    private String userId;
    private String sessionDetails;
    private String docId;
    private String docName;
    private Long docKeyPoid;
}
