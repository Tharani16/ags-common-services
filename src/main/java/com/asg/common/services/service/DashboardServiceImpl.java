package com.asg.common.services.service;


import com.asg.common.services.dto.*;
import com.asg.common.services.entity.DashboardEntity;
import com.asg.common.services.repository.CustomDashboardRepository;
import com.asg.common.services.repository.DashboardRepository;
import com.asg.common.services.service.impl.DashboardService;
import com.asg.common.lib.security.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

   private final DashboardRepository dashboardRepository;
   private final CustomDashboardRepository customDashboardRepository;

   @Autowired
    public DashboardServiceImpl(DashboardRepository dashboardRepository, CustomDashboardRepository customDashboardRepository) {
        this.dashboardRepository = dashboardRepository;
       this.customDashboardRepository = customDashboardRepository;
   }


    @Override
    public PendingApprovalResponse getDashboardEntity() {
        // Use logged-in context instead of passing POIDs from FE
        Long groupPoid = UserContext.getGroupPoid();
        Long companyPoid = UserContext.getCompanyPoid();
        Long userPoid = UserContext.getUserPoid();

        // FE will handle filtering/sorting, so return full list
        List<DashboardEntity> dashboardEntities = dashboardRepository.getPendingApprovals(groupPoid, companyPoid, userPoid);

        List<PendingApprovalsDto> pendingApprovalsDtos = dashboardEntities.stream()
                .map(this::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Create and populate response
        PendingApprovalResponse response = new PendingApprovalResponse();
        response.setPendingApprovals(pendingApprovalsDtos);
        response.setPageNumber(0);
        response.setPageSize(pendingApprovalsDtos.size());
        response.setTotalElements(dashboardEntities.size());
        response.setTotalPages(1);
       return response;
    }

    private PendingApprovalsDto fromEntity(DashboardEntity entity) {
        if (entity == null) {
            return null;
        }
        PendingApprovalsDto dto = new PendingApprovalsDto();
        BeanUtils.copyProperties(entity, dto);
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