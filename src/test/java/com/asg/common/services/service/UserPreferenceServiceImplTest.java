package com.asg.common.services.service;

import com.asg.common.lib.security.util.UserContext;
import com.asg.common.services.dto.RecentMenuDto;
import com.asg.common.services.dto.UserPreferenceRequest;
import com.asg.common.services.dto.UserProfileSettingDto;
import com.asg.common.services.service.impl.UserPreferenceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private CallableStatement callableStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserPreferenceServiceImpl userPreferenceService;

    @BeforeEach
    void setUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void getUserPreferences_Success() throws SQLException {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserPoid).thenReturn(123L);
            when(connection.prepareCall(anyString())).thenReturn(callableStatement);
            when(callableStatement.getObject(2)).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true, true, false);
            when(resultSet.getString("SETTINGS_NAME")).thenReturn("Theme", "Language");
            when(resultSet.getString("SETTINGS_VALUE")).thenReturn("Dark", "English");

            List<UserProfileSettingDto> result = userPreferenceService.getUserPreferences();

            assertEquals(2, result.size());
            assertEquals("Theme", result.get(0).settingsName());
            assertEquals("Dark", result.get(0).settingsValue());
            verify(callableStatement).setLong(1, 123L);
        }
    }

    @Test
    void updateUserPreferences_Success() throws SQLException {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserPoid).thenReturn(123L);
            when(connection.prepareCall(anyString())).thenReturn(callableStatement);

            UserPreferenceRequest request = new UserPreferenceRequest(Arrays.asList(
                    new UserPreferenceRequest.UserPreferenceItem("Theme", "Light")
            ));

            assertDoesNotThrow(() -> userPreferenceService.updateUserPreferences(request));

            verify(callableStatement).setLong(1, 123L);
            verify(callableStatement).setString(2, "Theme");
            verify(callableStatement).setString(3, "Light");
        }
    }

    @Test
    void getRecentMenus_Success() throws SQLException {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserId).thenReturn("user123");
            userContextMock.when(UserContext::getUserPoid).thenReturn(123L);
            when(connection.prepareCall(anyString())).thenReturn(callableStatement);
            when(callableStatement.getObject(3)).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true, false);
            when(resultSet.getString("MENU_ID")).thenReturn("M001");
            when(resultSet.getString("MENU_NAME")).thenReturn("Test Menu");
            when(resultSet.getString("MENU_LEVEL")).thenReturn("1");
            when(resultSet.getString("MENU_GROUP")).thenReturn("Group1");
            when(resultSet.getString("TASKFLOW_URL")).thenReturn("/test");
            when(resultSet.getString("DOC_TYPE")).thenReturn("DOC");
            when(resultSet.getString("MODULE_ID")).thenReturn("MOD1");

            List<RecentMenuDto> result = userPreferenceService.getRecentMenus();

            assertEquals(1, result.size());
            assertEquals("M001", result.get(0).menuId());
            verify(callableStatement).setString(1, "user123");
            verify(callableStatement).setLong(2, 123L);
        }
    }

    @Test
    void updateRecentMenu_DocumentSuccess() throws SQLException {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserPoid).thenReturn(123L);
            when(connection.prepareCall(anyString())).thenReturn(callableStatement);

            assertDoesNotThrow(() -> userPreferenceService.updateRecentMenu("DOC123", true));

            verify(callableStatement).setLong(1, 123L);
            verify(callableStatement).setString(2, "RecentDocument");
            verify(callableStatement).setString(3, "DOC123");
        }
    }

    @Test
    void updateRecentMenu_ReportSuccess() throws SQLException {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserPoid).thenReturn(123L);
            when(connection.prepareCall(anyString())).thenReturn(callableStatement);

            assertDoesNotThrow(() -> userPreferenceService.updateRecentMenu("RPT456", false));

            verify(callableStatement).setLong(1, 123L);
            verify(callableStatement).setString(2, "RecentReport");
            verify(callableStatement).setString(3, "RPT456");
        }
    }
}