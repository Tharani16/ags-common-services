package com.asg.common.services.service.impl;

import com.asg.common.services.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface DashboardService {

    PendingApprovalResponse getDashboardEntity(Long groupPoid, Long companyPoid, Long userPoid, Pageable pageable);

    List<UserApprovalSubmitStatusDto> fetchUserSubmitStatus(
            String userPoid,
            String status,
            Date fromDate,
            Date toDate
    );

    List<RecentDocumentDto> fetchRecentDocuments(String userId, Long userPoid);

    List<FavoriteMenuDto> fetchFavoriteMenu(String userId, Long userPoid);

    List<ApprovalPendingDto> fetchApprovalPendingList(
            String userPoid,
            String status,
            Date fromDate,
            Date toDate
    );

    List<WeeklyTransactionDto> fetchWeeklyTransactions(
            String loginUserPoid,
            String periodFrom,
            String periodTo
    );
}