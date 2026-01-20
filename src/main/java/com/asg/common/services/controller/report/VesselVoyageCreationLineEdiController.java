package com.asg.common.services.controller.report;

import com.asg.common.lib.annotation.AllowedAction;
import com.asg.common.lib.dto.excel.ExcelFileData;
import com.asg.common.lib.enums.UserRolesRightsEnum;
import com.asg.common.lib.enums.VesselVoyageEdiReportType;
import com.asg.common.lib.service.ExcelExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/vessel-voyage-creation-line-edi")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vessel Voyage Creation Line EDI Management", description = "APIs for managing vessel voyage creation line EDI")
public class VesselVoyageCreationLineEdiController {

    private final ExcelExportService excelExportService;

    @AllowedAction(UserRolesRightsEnum.PRINT)
    @Operation(summary = "Generate Excel for Vessel Voyage EDI Reports")
    @GetMapping("/excel/{reportType}/{transactionPoid}")
    public ResponseEntity<?> generateExcel(
            @PathVariable VesselVoyageEdiReportType reportType,
            @Parameter(description = "Transaction POID", example = "476")
            @PathVariable Long transactionPoid) {
        try {
            ExcelFileData data = excelExportService.generateExcel(reportType.getDocId(), String.valueOf(transactionPoid), null, reportType.getFileName());
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