package com.asg.common.services.service;


import com.asg.common.services.dto.RecentMenuDto;
import com.asg.common.services.dto.UserPreferenceRequest;
import com.asg.common.services.dto.UserProfileSettingDto;

import java.util.List;

public interface UserPreferenceService {
    List<UserProfileSettingDto> getUserPreferences();
    void updateUserPreferences(UserPreferenceRequest request);
    List<RecentMenuDto> getRecentMenus();
    void updateRecentMenu(String documentId, Boolean isDocument);
}