package com.asg.common.services.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalPendingDto {
    private Long docKeyPoid;
    private String docId;
    private String docName;
    private String docShortName;
    private String routeName;
    private String docRef;
    private String actionType;
    private LocalDateTime actionedDatetime;
}
