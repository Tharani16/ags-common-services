package com.asg.common.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApprovalSubmitStatusDto {
    private String submittedBy;
    private String submittedByName;
    private String docName;
    private String docId;
    private String docKeyPoid;
    private String currentDocStatus;
    private String nextApproverName;
    private java.util.Date submitDate;
    private String status;
}
