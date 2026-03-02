package com.asg.common.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalActionResponse {
    
    private String status;
    private String message;
    private String approverUserPoid;
    private Long approvalPoid;
    private boolean success;
}
