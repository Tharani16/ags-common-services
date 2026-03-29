package com.asg.common.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingApprovalResponse {
    private List<PendingApprovalsDto> pendingApprovals;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}