package com.asg.common.services.dto;

import lombok.Data;

@Data
public class WeeklyTransactionDto {
    private String docShortName;
    private String docActionType;
    private Long docCount;
}
