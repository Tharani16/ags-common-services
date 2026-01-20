package com.asg.common.services.controller.report;

import com.asg.common.lib.dto.excel.ExcelFileData;
import com.asg.common.lib.service.DynamicReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/report")
@RequiredArgsConstructor
public class DynamicReportController {

    private final DynamicReportService dynamicReportService;

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam String rptDocId,
            @RequestParam String rptName,
            @RequestBody(required = false) Map<String, Object> filters) {
        ExcelFileData excelData = dynamicReportService.exportToExcel(rptDocId, filters, rptName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + excelData.getFileName() + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData.getContent());
    }
}