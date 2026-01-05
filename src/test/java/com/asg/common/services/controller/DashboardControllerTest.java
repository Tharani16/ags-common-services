package com.asg.common.services.controller;

import com.asg.common.lib.security.util.UserContext;
import com.asg.common.services.dto.*;
import com.asg.common.services.service.impl.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
        objectMapper = new ObjectMapper();
    }


    @Test
    void getUserApprovalSubmitStatus_ShouldReturnList() throws Exception {
        List<UserApprovalSubmitStatusDto> expected = Arrays.asList(new UserApprovalSubmitStatusDto());

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserPoid).thenReturn(1L);
            when(dashboardService.fetchUserSubmitStatus(anyString(), anyString(), any(), any()))
                    .thenReturn(expected);

            mockMvc.perform(get("/api/v1/dashboard/submit-status")
                            .param("documentId", "DOC-001")
                            .param("actionRequested", "APPROVE"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getRecentDocuments_ShouldReturnList() throws Exception {
        List<RecentDocumentDto> expected = Arrays.asList(new RecentDocumentDto());

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn("user1");
            mockedUserContext.when(UserContext::getUserPoid).thenReturn(1L);
            when(dashboardService.fetchRecentDocuments(anyString(), anyLong()))
                    .thenReturn(expected);

            mockMvc.perform(get("/api/v1/dashboard/recent-documents")
                            .param("documentId", "DOC-001")
                            .param("actionRequested", "VIEW"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getFavoriteMenu_ShouldReturnList() throws Exception {
        List<FavoriteMenuDto> expected = Arrays.asList(new FavoriteMenuDto());

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn("user1");
            mockedUserContext.when(UserContext::getUserPoid).thenReturn(1L);
            when(dashboardService.fetchFavoriteMenu(anyString(), anyLong()))
                    .thenReturn(expected);

            mockMvc.perform(get("/api/v1/dashboard/favorite-menu")
                            .param("documentId", "DOC-001")
                            .param("actionRequested", "VIEW"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getApprovalPendingList_ShouldReturnList() throws Exception {
        List<ApprovalPendingDto> expected = Arrays.asList(new ApprovalPendingDto());

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserPoid).thenReturn(1L);
            when(dashboardService.fetchApprovalPendingList(anyString(), anyString(), any(), any()))
                    .thenReturn(expected);

            mockMvc.perform(get("/api/v1/dashboard/approval-pending-list")
                            .param("fromDate", "2024-01-01")
                            .param("toDate", "2024-12-31")
                            .param("documentId", "DOC-001")
                            .param("actionRequested", "APPROVE"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getWeeklyTransactions_ShouldReturnList() throws Exception {
        List<WeeklyTransactionDto> expected = Arrays.asList(new WeeklyTransactionDto());

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserPoid).thenReturn(1L);
            when(dashboardService.fetchWeeklyTransactions(anyString(), anyString(), anyString()))
                    .thenReturn(expected);

            mockMvc.perform(get("/api/v1/dashboard/weekly-transactions")
                            .param("periodFrom", "2024-01-01")
                            .param("periodTo", "2024-01-07")
                            .param("documentId", "DOC-001")
                            .param("actionRequested", "VIEW"))
                    .andExpect(status().isOk());
        }
    }
}