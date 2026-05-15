package com.asg.common.services.dto;

import com.asg.common.services.enums.ApprovalAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalActionRequest {
    
    private Long loginGroupPoid;
    private Long companyPoid;
    private Long userPoid;
    
    @NotBlank(message = "Document ID is required")
    private String docId;
    
    @NotNull(message = "Document key POID is required")
    private Long docKeyPoid;
    
    private ApprovalAction approvalAction;
    
    private String comments;
    
    private String docName;
    
    private String docRef;
    
    private LocalDateTime docDate;
    
    private Long userRolePoid;
}
