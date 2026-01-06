package com.asg.common.services.controller;

import com.asg.common.services.dto.RecentMenuDto;
import com.asg.common.services.dto.UpdateRecentMenuRequest;
import com.asg.common.services.dto.UserPreferenceRequest;
import com.asg.common.services.dto.UserProfileSettingDto;
import com.asg.common.services.service.UserPreferenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserPreferenceControllerTest {

    @Mock
    private UserPreferenceService userPreferenceService;

    @InjectMocks
    private UserPreferenceController userPreferenceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userPreferenceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserPreferences_Success() throws Exception {
        List<UserProfileSettingDto> preferences = Arrays.asList(
                new UserProfileSettingDto("Theme", "Dark"),
                new UserProfileSettingDto("Language", "English")
        );
        when(userPreferenceService.getUserPreferences()).thenReturn(preferences);

        mockMvc.perform(get("/v1/user-preference"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User preferences fetched successfully"));

        verify(userPreferenceService, times(1)).getUserPreferences();
    }

    @Test
    void getUserPreferences_Exception() throws Exception {
        when(userPreferenceService.getUserPreferences()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/v1/user-preference"))
                .andExpect(status().isInternalServerError());

        verify(userPreferenceService, times(1)).getUserPreferences();
    }

    @Test
    void updateUserPreferences_Success() throws Exception {
        UserPreferenceRequest request = new UserPreferenceRequest(Arrays.asList(
                new UserPreferenceRequest.UserPreferenceItem("Theme", "Light")
        ));
        doNothing().when(userPreferenceService).updateUserPreferences(any());

        mockMvc.perform(put("/v1/user-preference")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User preferences updated successfully"));

        verify(userPreferenceService, times(1)).updateUserPreferences(any());
    }

    @Test
    void updateUserPreferences_Exception() throws Exception {
        UserPreferenceRequest request = new UserPreferenceRequest(Arrays.asList(
                new UserPreferenceRequest.UserPreferenceItem("Theme", "Light")
        ));
        doThrow(new RuntimeException("Update failed")).when(userPreferenceService).updateUserPreferences(any());

        mockMvc.perform(put("/v1/user-preference")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userPreferenceService, times(1)).updateUserPreferences(any());
    }

    @Test
    void getRecentMenus_Success() throws Exception {
        List<RecentMenuDto> recentMenus = Arrays.asList(
                new RecentMenuDto("M001", "Menu 1", "1", "Group1", "/url1", "DOC", "MOD1"),
                new RecentMenuDto("M002", "Menu 2", "2", "Group2", "/url2", "RPT", "MOD2")
        );
        when(userPreferenceService.getRecentMenus()).thenReturn(recentMenus);

        mockMvc.perform(get("/v1/user-preference/recent-menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recent menu list fetched successfully"));

        verify(userPreferenceService, times(1)).getRecentMenus();
    }

    @Test
    void getRecentMenus_Exception() throws Exception {
        when(userPreferenceService.getRecentMenus()).thenThrow(new RuntimeException("Fetch failed"));

        mockMvc.perform(get("/v1/user-preference/recent-menu"))
                .andExpect(status().isInternalServerError());

        verify(userPreferenceService, times(1)).getRecentMenus();
    }

    @Test
    void updateRecentMenu_Success() throws Exception {
        UpdateRecentMenuRequest request = new UpdateRecentMenuRequest("DOC123", true);
        doNothing().when(userPreferenceService).updateRecentMenu(anyString(), anyBoolean());

        mockMvc.perform(put("/v1/user-preference/recent-menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recent menu updated successfully"));

        verify(userPreferenceService, times(1)).updateRecentMenu("DOC123", true);
    }

    @Test
    void updateRecentMenu_Exception() throws Exception {
        UpdateRecentMenuRequest request = new UpdateRecentMenuRequest("DOC123", true);
        doThrow(new RuntimeException("Update failed")).when(userPreferenceService).updateRecentMenu(anyString(), anyBoolean());

        mockMvc.perform(put("/v1/user-preference/recent-menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userPreferenceService, times(1)).updateRecentMenu("DOC123", true);
    }

    @Test
    void updateRecentMenu_DirectCall_Success() {
        UpdateRecentMenuRequest request = new UpdateRecentMenuRequest("RPT456", false);
        doNothing().when(userPreferenceService).updateRecentMenu(anyString(), anyBoolean());

        ResponseEntity<?> response = userPreferenceController.updateRecentMenu(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userPreferenceService, times(1)).updateRecentMenu("RPT456", false);
    }

    @Test
    void updateRecentMenu_DirectCall_Exception() {
        UpdateRecentMenuRequest request = new UpdateRecentMenuRequest("DOC789", true);
        doThrow(new RuntimeException("Service error")).when(userPreferenceService).updateRecentMenu(anyString(), anyBoolean());

        ResponseEntity<?> response = userPreferenceController.updateRecentMenu(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userPreferenceService, times(1)).updateRecentMenu("DOC789", true);
    }
}
