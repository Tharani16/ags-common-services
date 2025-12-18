package com.asg.common.services.dto;

import lombok.Data;

@Data
public class FavoriteMenuRequest {
    private String userId;
    private Long userPoid;
    private String menuGroup;
    private String selectedDocIds;
}
