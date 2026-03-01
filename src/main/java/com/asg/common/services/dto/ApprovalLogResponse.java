package com.asg.common.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalLogResponse {
    private List<Map<String, String>> logs;
    private List<ColumnInfo> columns;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ColumnInfo {
        private String columnName;
        private String columnWidth;
    }
}
