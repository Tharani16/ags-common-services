package com.asg.common.services.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

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
    private Date docDate;
}
