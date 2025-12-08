package com.asg.common.services.dto;

import lombok.Data;
import java.util.List;

@Data
public class PendingApprovalResponse<T> {
    private List<PendingApprovalsDto> pendingApprovals;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}