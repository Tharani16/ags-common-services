package com.asg.common.services.controller;

import com.asg.common.services.dto.RecentMenuDto;
import com.asg.common.services.dto.UpdateRecentMenuRequest;
import com.asg.common.services.dto.UserPreferenceRequest;
import com.asg.common.services.dto.UserProfileSettingDto;
import com.asg.common.services.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.asg.common.lib.dto.response.ApiResponse.internalServerError;
import static com.asg.common.lib.dto.response.ApiResponse.success;

@RestController
@RequestMapping("/v1/user-preference")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UserPreferenceController {

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Operation(
            summary = "Get User Preferences",
            description = """
                    Retrieve the list of user preferences for the logged-in user.
                    Returns all settings configured for the authenticated user.
                    """
    )
    @GetMapping()
    public ResponseEntity<?> getUserPreferences() {
        try {
            List<UserProfileSettingDto> preferences = userPreferenceService.getUserPreferences();
            return success("User preferences fetched successfully", preferences);
        } catch (Exception ex) {
            log.error("Failed to fetch user preferences: {}", ex.getMessage());
            return internalServerError("Failed to fetch user preferences: " + ex.getMessage());
        }
    }

    @Operation(
            summary = "Update User Preferences",
            description = """
                    Update user preferences for the logged-in user.
                    Allows bulk update of multiple preference settings.
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = """
                    - **Request Body Fields:**
                                           • preferences: Array of preference items to update
                                           • settingsName: Name of the setting (e.g., 'Theme', 'Language')
                                           • settingsValue: Value of the setting (e.g., 'Dark', 'English')
                    """,
            content = @Content(
                    schema = @Schema(implementation = UserPreferenceRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "User Preferences Update",
                                    value = """
                                            {
                                              "preferences": [
                                                {
                                                  "settingsName": "Theme",
                                                  "settingsValue": "Dark"
                                                },
                                                {
                                                  "settingsName": "Language",
                                                  "settingsValue": "English"
                                                }
                                              ]
                                            }
                                            """
                            )
                    }
            )
    )
    @PutMapping()
    public ResponseEntity<?> updateUserPreferences(@Valid @RequestBody UserPreferenceRequest request) {
        try {
            userPreferenceService.updateUserPreferences(request);
            return success("User preferences updated successfully", null);
        } catch (Exception ex) {
            log.error("Failed to update user preferences: {}", ex.getMessage());
            return internalServerError("Failed to update user preferences: " + ex.getMessage());
        }
    }

    @Operation(
            summary = "Get Recent Menu",
            description = """
                    Retrieve the list of user's recent menus.
                    Returns both recent documents and reports accessed by the user.
                    """
    )
    @GetMapping("/recent-menu")
    public ResponseEntity<?> getRecentMenus() {
        try {
            List<RecentMenuDto> recentMenus = userPreferenceService.getRecentMenus();
            return success("Recent menu list fetched successfully", recentMenus);
        } catch (Exception ex) {
            return internalServerError("Failed to fetch recent menus: " + ex.getMessage());
        }
    }

    @Operation(
            summary = "Update Recent Menu",
            description = """
                    Update a user's recent menu settings.
                    Adds a document or report to the user's recent items list.
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = """
                    - **Request Body Fields:**
                                           • documentId: Unique identifier for the document/report
                                           • isDocument: Boolean flag (true for document, false for report)
                    """,
            content = @Content(
                    schema = @Schema(implementation = UpdateRecentMenuRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Recent Document Update",
                                    value = """
                                            {
                                              "documentId": "000-007",
                                              "isDocument": true
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Recent Report Update",
                                    value = """
                                            {
                                              "documentId": "RPT-001",
                                              "isDocument": false
                                            }
                                            """
                            )
                    }
            )
    )
    @PutMapping("/recent-menu")
    public ResponseEntity<?> updateRecentMenu(@Valid @RequestBody UpdateRecentMenuRequest request) {
        try {
            userPreferenceService.updateRecentMenu(request.documentId(), request.isDocument());
            return success("Recent menu updated successfully", null);
        } catch (Exception ex) {
            return internalServerError("Failed to update recent menu: " + ex.getMessage());
        }
    }
}