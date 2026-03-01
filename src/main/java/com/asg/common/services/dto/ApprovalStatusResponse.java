package com.asg.common.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalStatusResponse {
    
    private String statusCode;
    private String statusDetails;
    private String approverUsers;
    private String color;
    
    // Button states
    private boolean submitForApprovalEnabled;
    private boolean recallForChangeEnabled;
    private boolean returnForCorrectionEnabled;
    private boolean approveEnabled;
    private boolean approveWithCommentsEnabled;
    private boolean rejectEnabled;
    private boolean specialApproveEnabled;
    private boolean specialSubmitEnabled;
    private boolean approvalMenuVisible;
}
