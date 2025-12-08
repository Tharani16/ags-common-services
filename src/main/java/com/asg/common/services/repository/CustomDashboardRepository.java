package com.asg.common.services.repository;

import com.asg.common.services.dto.*;

import java.util.Date;
import java.util.List;

public interface CustomDashboardRepository {


    List<UserApprovalSubmitStatusDto> getUserApprovalSubmitStatus(
            String userPoid,
            String status,
            Date fromDate,
            Date toDate
    );

    List<RecentDocumentDto> getRecentDocumentList(String userId, Long userPoid);

    List<FavoriteMenuDto> getFavoriteMenuList(String userId, Long userPoid);

    List<ApprovalPendingDto> getApprovalPendingList(
            String userPoid,
            String status,
            Date fromDate,
            Date toDate
    );

    List<WeeklyTransactionDto> getWeeklyTransactions(
            String loginUserPoid,
            String periodFrom,
            String periodTo
    );

}
