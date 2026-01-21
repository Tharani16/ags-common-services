package com.asg.common.services.controller.report;

import com.asg.common.lib.annotation.AllowedAction;
import com.asg.common.lib.dto.excel.ExcelFileData;
import com.asg.common.lib.enums.ContainerInventoryReportType;
import com.asg.common.lib.enums.UserRolesRightsEnum;
import com.asg.common.lib.service.ExcelExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/container-inventory-report")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Container Inventory Report Management", description = "APIs for container inventory reports")
public class ContainerInventoryReportController {

    private final ExcelExportService excelExportService;

    @AllowedAction(UserRolesRightsEnum.PRINT)
    @Operation(summary = "Generate Excel for Container Inventory Reports")
    @GetMapping("/excel/{reportType}")
    public ResponseEntity<?> generateExcel(
            @PathVariable ContainerInventoryReportType reportType,
            @Parameter(description = "Line POID", required = true, example = "123")
            @RequestParam Long linePoid,
            @Parameter(description = "From Date", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "To Date", required = true, example = "2025-01-31")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("P_LINE_POID", linePoid);
            parameters.put("P_FROM_DATE", fromDate);
            parameters.put("P_TO_DATE", toDate);
            
            ExcelFileData data = excelExportService.generateExcel(reportType.getDocId(), null, parameters, reportType.getFileName());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + data.getFileName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data.getContent());
        } catch (Exception e) {
            log.error("Failed to generate Excel for {}", reportType, e);
            return ResponseEntity.status(500).body("Failed to generate Excel: " + e.getMessage());
        }
    }
}