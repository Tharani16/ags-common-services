package com.asg.common.services.controller.report;

import com.asg.common.lib.annotation.AllowedAction;
import com.asg.common.lib.enums.UserRolesRightsEnum;
import com.asg.common.services.service.DynamicReportPrintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/dynamic")
@RequiredArgsConstructor
@Slf4j
public class DynamicPrintController {

    private final DynamicReportPrintService dynamicReportPrintService;

    @AllowedAction(UserRolesRightsEnum.PRINT)
    @PostMapping("/print")
    public ResponseEntity<byte[]> printReport(
            @RequestParam String rptDocId,
            @RequestParam String rptName,
            @RequestBody(required = false) Map<String, Object> rptParams) throws Exception {
        byte[] pdfBytes = dynamicReportPrintService.generateReportPdf(rptDocId, rptParams);
        String downloadFileName = (rptName != null && !rptName.isEmpty()) ? rptName + ".pdf" : "report.pdf";
        log.info("PDF generated successfully for report: {}", downloadFileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}