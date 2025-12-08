package com.asg.common.services.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ApprovalPendingDto {
    private String docKeyPoid;
    private String docId;
    private String docName;
    private String actionType;
    private Date actionedDatetime;
}
