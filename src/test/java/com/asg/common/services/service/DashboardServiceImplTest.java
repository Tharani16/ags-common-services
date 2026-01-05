package com.asg.common.services.service;

import com.asg.common.services.dto.*;
import com.asg.common.services.entity.DashboardEntity;
import com.asg.common.services.repository.CustomDashboardRepository;
import com.asg.common.services.repository.DashboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private CustomDashboardRepository customDashboardRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private DashboardEntity testEntity;
    private Pageable pageable;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testEntity = new DashboardEntity();
        testEntity.setId(1L);
        testEntity.setCompanyPoid(101L);
        testEntity.setGroupPoid(201L);
        testEntity.setDocId("DOC-001");
        testEntity.setDocShortName("Test Doc");
        testEntity.setDocDate(new Date());
        testEntity.setDocSummaryInfo("Test Summary");
        testEntity.setUserId("user1");

        pageable = PageRequest.of(0, 10);
        testDate = new Date();
    }

    @Test
    void getDashboardEntity_WithValidInput_ShouldReturnPaginatedResponse() {
        List<DashboardEntity> entities = Arrays.asList(testEntity);
        when(dashboardRepository.getPendingApprovals(1L, 1L, 1L)).thenReturn(entities);

        PendingApprovalResponse result = dashboardService.getDashboardEntity(1L, 1L, 1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getPendingApprovals().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(1, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void getDashboardEntity_WithEmptyResult_ShouldReturnEmptyResponse() {
        when(dashboardRepository.getPendingApprovals(anyLong(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        PendingApprovalResponse result = dashboardService.getDashboardEntity(1L, 1L, 1L, pageable);

        assertNotNull(result);
        assertTrue(result.getPendingApprovals().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void fetchUserSubmitStatus_ShouldReturnList() {
        List<UserApprovalSubmitStatusDto> expected = Arrays.asList(
                UserApprovalSubmitStatusDto.builder().submittedBy("user1").build());
        when(customDashboardRepository.getUserApprovalSubmitStatus("1", "PENDING", testDate, testDate))
                .thenReturn(expected);

        List<UserApprovalSubmitStatusDto> result = dashboardService.fetchUserSubmitStatus("1", "PENDING", testDate, testDate);

        assertEquals(expected, result);
    }

    @Test
    void fetchRecentDocuments_ShouldReturnList() {
        List<RecentDocumentDto> expected = Arrays.asList(new RecentDocumentDto());
        when(customDashboardRepository.getRecentDocumentList("user1", 1L)).thenReturn(expected);

        List<RecentDocumentDto> result = dashboardService.fetchRecentDocuments("user1", 1L);

        assertEquals(expected, result);
    }

    @Test
    void fetchFavoriteMenu_ShouldReturnList() {
        List<FavoriteMenuDto> expected = Arrays.asList(new FavoriteMenuDto());
        when(customDashboardRepository.getFavoriteMenuList("user1", 1L)).thenReturn(expected);

        List<FavoriteMenuDto> result = dashboardService.fetchFavoriteMenu("user1", 1L);

        assertEquals(expected, result);
    }

    @Test
    void fetchApprovalPendingList_ShouldReturnList() {
        List<ApprovalPendingDto> expected = Arrays.asList(new ApprovalPendingDto());
        when(customDashboardRepository.getApprovalPendingList("1", "PENDING", testDate, testDate))
                .thenReturn(expected);

        List<ApprovalPendingDto> result = dashboardService.fetchApprovalPendingList("1", "PENDING", testDate, testDate);

        assertEquals(expected, result);
    }

    @Test
    void fetchWeeklyTransactions_ShouldReturnList() {
        List<WeeklyTransactionDto> expected = Arrays.asList(new WeeklyTransactionDto());
        when(customDashboardRepository.getWeeklyTransactions("1", "2024-01", "2024-02"))
                .thenReturn(expected);

        List<WeeklyTransactionDto> result = dashboardService.fetchWeeklyTransactions("1", "2024-01", "2024-02");

        assertEquals(expected, result);
    }
}