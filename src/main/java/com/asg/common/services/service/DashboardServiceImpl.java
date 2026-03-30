package com.asg.common.services.service;


import com.asg.common.services.dto.*;
import com.asg.common.services.repository.CustomDashboardRepository;
import com.asg.common.services.service.impl.DashboardService;
import com.asg.common.lib.security.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

   private final CustomDashboardRepository customDashboardRepository;

   @Autowired
    public DashboardServiceImpl(CustomDashboardRepository customDashboardRepository) {
       this.customDashboardRepository = customDashboardRepository;
   }


    @Override
    public PendingApprovalResponse getDashboardEntity() {
        Long userPoid = UserContext.getUserPoid();
        // PROC_GLOB_APPROVAL_PENDING_V2 uses P_FROM_DATE / P_TO_DATE in the APPROVED branch; pass current week so Oracle does not receive NULL dates.
        LocalDate today = LocalDate.now();
        Date weekStart = java.sql.Date.valueOf(today.with(DayOfWeek.MONDAY));
        Date weekEnd = java.sql.Date.valueOf(today.with(DayOfWeek.SUNDAY));

        List<ApprovalPendingDto> approvalPendingList = customDashboardRepository.getApprovalPendingList(
                String.valueOf(userPoid),
                "PENDING",
                weekStart,
                weekEnd
        );

        List<PendingApprovalsDto> pendingApprovalsDtos = new ArrayList<>();
        long rowId = 1;
        for (ApprovalPendingDto row : approvalPendingList) {
            PendingApprovalsDto dto = fromApprovalPending(row, rowId++);
            if (dto != null) {
                pendingApprovalsDtos.add(dto);
            }
        }

        // Create and populate response
        PendingApprovalResponse response = new PendingApprovalResponse();
        response.setPendingApprovals(pendingApprovalsDtos);
        response.setPageNumber(0);
        response.setPageSize(pendingApprovalsDtos.size());
        response.setTotalElements(approvalPendingList.size());
        response.setTotalPages(1);
       return response;
    }

    private PendingApprovalsDto fromApprovalPending(ApprovalPendingDto source, long id) {
        if (source == null) {
            return null;
        }
        PendingApprovalsDto dto = new PendingApprovalsDto();
        dto.setId(id);
        dto.setDocId(source.getDocId());
        dto.setDocKeyPoid(source.getDocKeyPoid());
        dto.setDocName(source.getDocName());
        dto.setDocShortName(source.getDocShortName());
        dto.setDocRef(source.getDocRef());
        dto.setRouteName(source.getRouteName());
        dto.setActionStatus(source.getActionType());
        if (source.getActionedDatetime() != null) {
            dto.setDatetime(java.sql.Timestamp.valueOf(source.getActionedDatetime()));
        }
        return dto;
    }

    @Override
    public List<UserApprovalSubmitStatusDto> fetchUserSubmitStatus(
            String userPoid,
            String status,
            Date fromDate,
            Date toDate
    ) {
        return customDashboardRepository.getUserApprovalSubmitStatus(userPoid, status, fromDate, toDate);
    }


    @Override
    public List<RecentDocumentDto> fetchRecentDocuments(String userId, Long userPoid) {
        return customDashboardRepository.getRecentDocumentList(userId, userPoid);
    }

    @Override
    public List<RecentDocumentDto> fetchRecentTransactions(String userId, Long userPoid) {
        DateTimeFormatter formatter = new java.time.format.DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd-MMM-yyyy hh:mm:ss a")
                .toFormatter(java.util.Locale.ENGLISH);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        List<RecentDocumentDto> all = customDashboardRepository.getRecentDocumentList(userId, userPoid);

        List<RecentDocumentDto> filtered = all.stream()
                .filter(dto -> {
                    if (dto.getDocDate() == null) return false;
                    try {
                        LocalDate docDate = java.time.LocalDateTime.parse(dto.getDocDate(), formatter).toLocalDate();
                        return !docDate.isBefore(weekStart) && !docDate.isAfter(weekEnd);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .sorted(Comparator.comparing(dto -> {
                    try {
                        return java.time.LocalDateTime.parse(dto.getDocDate(), formatter);
                    } catch (Exception e) {
                        return java.time.LocalDateTime.MIN;
                    }
                }, Comparator.reverseOrder()))
                .limit(15)
                .collect(Collectors.toList());

        return filtered;
    }


    @Override
    public List<FavoriteMenuDto> fetchFavoriteMenu(String userId, Long userPoid) {
        return customDashboardRepository.getFavoriteMenuList(userId, userPoid);
    }


    @Override
    public List<ApprovalPendingDto> fetchApprovalPendingList(
            String userPoid,
            String status,
            Date fromDate,
            Date toDate
    ) {
        return customDashboardRepository.getApprovalPendingList(userPoid, status, fromDate, toDate);
    }

    @Override
    public List<WeeklyTransactionDto> fetchWeeklyTransactions(String loginUserPoid) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MMM-yyyy", java.util.Locale.ENGLISH);
        String periodFrom = today.with(DayOfWeek.MONDAY).format(formatter);
        String periodTo = today.with(DayOfWeek.SUNDAY).format(formatter);
        return customDashboardRepository.getWeeklyTransactions(loginUserPoid, periodFrom, periodTo);
    }
}