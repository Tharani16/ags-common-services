package com.asg.common.services.controller;

import com.asg.common.lib.dto.request.LogFilterRequest;
import com.asg.common.lib.dto.response.ApiResponse;
import com.asg.common.lib.dto.response.PagedLogResponse;
import com.asg.common.lib.service.LoggingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/log")
@RequiredArgsConstructor
@Slf4j
public class LoggingController {

    private final LoggingService loggingService;

    // ----------------- SUMMARY LOG -----------------
    @Operation(
            summary = "Get Log Summary",
            description = "Fetch the summary of logs for a given document."
    )
    @GetMapping("/{docId}/{docKeyPoid}/summary")
    public Object getSummaryLog(
            @Parameter(description = "Document ID", required = true)
            @PathVariable String docId,

            @Parameter(description = "Document Key Poid", required = true)
            @PathVariable Long docKeyPoid
    ) {
        try {
            List<?> summary = loggingService.getLogSummary(docId, docKeyPoid);
            return ApiResponse.success("Log summary fetched successfully", summary);
        } catch (Exception e) {
            log.error("Error fetching log summary for docId {}, key {}", docId, docKeyPoid, e);
            return ApiResponse.internalServerError("Failed to fetch log summary: " + e.getMessage());
        }
    }

    // ----------------- DETAIL LOG -----------------
    @Operation(
            summary = "Get Log Details",
            description = "Fetch detailed log entries for a given document."
    )
    @GetMapping("/{docId}/{docKeyPoid}/details")
    public Object getDetailLog(
            @Parameter(description = "Document ID", required = true)
            @PathVariable String docId,

            @Parameter(description = "Document Key Poid", required = true)
            @PathVariable Long docKeyPoid
    ) {
        try {
            List<?> details = loggingService.getDetailedLogs(docId, docKeyPoid);
            return ApiResponse.success("Log details fetched successfully", details);
        } catch (Exception e) {
            log.error("Error fetching detail log for docId {}, key {}", docId, docKeyPoid, e);
            return ApiResponse.internalServerError("Failed to fetch log details: " + e.getMessage());
        }
    }

    // ----------------- SUMMARY LOG WITH PAGINATION -----------------
    @Operation(
            summary = "Get Log Summary with Pagination",
            description = "Fetch paginated summary logs with optional filters."
    )
    @GetMapping("/{docId}/{docKeyPoid}/summary/paged")
    public Object getSummaryLogPaged(
            @PathVariable String docId,
            @PathVariable Long docKeyPoid,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "" + Integer.MAX_VALUE) Integer size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String searchText
    ) {
        try {
            LogFilterRequest filter = new LogFilterRequest();
            filter.setPage(page);
            filter.setSize(size);
            filter.setStartDate(startDate);
            filter.setEndDate(endDate);
            filter.setSearchText(searchText);

            PagedLogResponse response = loggingService.getLogSummaryPaged(docId, docKeyPoid, filter);
            return ApiResponse.success("Paginated log summary fetched successfully", response);
        } catch (Exception e) {
            log.error("Error fetching paginated log summary for docId {}, key {}", docId, docKeyPoid, e);
            return ApiResponse.internalServerError("Failed to fetch paginated log summary: " + e.getMessage());
        }
    }

    // ----------------- DETAIL LOG WITH PAGINATION -----------------
    @Operation(
            summary = "Get Log Details with Pagination",
            description = "Fetch paginated detailed logs with optional filters."
    )
    @GetMapping("/{docId}/{docKeyPoid}/details/paged")
    public Object getDetailLogPaged(
            @PathVariable String docId,
            @PathVariable Long docKeyPoid,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "" + Integer.MAX_VALUE) Integer size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String searchText
    ) {
        try {
            LogFilterRequest filter = new LogFilterRequest();
            filter.setPage(page);
            filter.setSize(size);
            filter.setStartDate(startDate);
            filter.setEndDate(endDate);
            filter.setSearchText(searchText);

            PagedLogResponse response = loggingService.getDetailedLogsPaged(docId, docKeyPoid, filter);
            return ApiResponse.success("Paginated log details fetched successfully", response);
        } catch (Exception e) {
            log.error("Error fetching paginated detail log for docId {}, key {}", docId, docKeyPoid, e);
            return ApiResponse.internalServerError("Failed to fetch paginated log details: " + e.getMessage());
        }
    }
}
