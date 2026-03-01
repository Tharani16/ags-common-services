package com.asg.common.services.service;

import com.asg.common.services.dto.ApprovalActionRequest;
import com.asg.common.services.dto.ApprovalActionResponse;
import com.asg.common.services.dto.ApprovalLogResponse;
import com.asg.common.services.dto.ApprovalStatusResponse;

public interface ApprovalService {
    
    ApprovalActionResponse executeApprovalAction(ApprovalActionRequest request);
    
    ApprovalStatusResponse getApprovalStatus(String docId, Long docKeyPoid);
    
    ApprovalLogResponse getApprovalLog(String docId, Long docKeyPoid);
}
