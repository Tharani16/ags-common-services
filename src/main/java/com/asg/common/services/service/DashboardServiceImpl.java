package com.asg.common.services.service;


import com.asg.common.services.dto.*;
import com.asg.common.services.entity.DashboardEntity;
import com.asg.common.services.repository.CustomDashboardRepository;
import com.asg.common.services.repository.DashboardRepository;
import com.asg.common.services.service.impl.DashboardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public PendingApprovalResponse getDashboardEntity(Long groupPoid, Long companyPoid, Long userPoid, Pageable pageable) {
        List<DashboardEntity> dashboardEntities=dashboardRepository.getPendingApprovals(groupPoid, companyPoid, userPoid);
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dashboardEntities.size());

        // Get sublist for the current page
        List<DashboardEntity> pageContent = dashboardEntities.subList(start, end);

        // Convert only the paginated entities to DTOs
        List<PendingApprovalsDto> pendingApprovalsDtos = pageContent.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());

        // Create and populate response
        PendingApprovalResponse response = new PendingApprovalResponse();
        response.setPendingApprovals(pendingApprovalsDtos);
        response.setPageNumber(pageable.getPageNumber());
        response.setPageSize(pendingApprovalsDtos.size());
        response.setTotalElements(dashboardEntities.size());
        response.setTotalPages((int) Math.ceil((double) dashboardEntities.size() / pageable.getPageSize()));
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
    public List<WeeklyTransactionDto> fetchWeeklyTransactions(
            String loginUserPoid,
            String periodFrom,
            String periodTo
    ) {
        return customDashboardRepository.getWeeklyTransactions(loginUserPoid, periodFrom, periodTo);
    }
}