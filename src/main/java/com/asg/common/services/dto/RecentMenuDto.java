package com.asg.common.services.dto;

public record RecentMenuDto(
        String menuId,
        String menuName,
        String menuLevel,
        String menuGroup,
        String taskflowUrl,
        String docType,
        String moduleId
) {}