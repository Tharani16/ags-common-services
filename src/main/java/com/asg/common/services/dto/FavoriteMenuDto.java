package com.asg.common.services.dto;

import lombok.Data;

@Data
public class FavoriteMenuDto {
    private Long menuId;
    private String menuName;
    private Long menuLevel;
    private String menuGroup;
    private String taskflowUrl;
    private String docType;
    private Long moduleId;
    private Long catSeqNo;
    private Long docSeqNo;
}
