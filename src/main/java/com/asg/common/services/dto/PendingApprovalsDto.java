package com.asg.common.services.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PendingApprovalsDto {

    private Long id;
    private Long companyPoid;
    private Long groupPoid;
    private String docId;
    private String docShortName;
    private Long docKeyPoid;
    private String docRef;
    private Date docDate;
    private String docSummaryInfo;
    private String userId;
    private  Long userRolePoid;
    private  Long userPoid;
    private  String actionStatus;
    private  String actionedBy;
    private String userName;
    private Date datetime;
    private String comments;
    //private String taskflowurl;

}
