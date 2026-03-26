package com.asg.common.services.controller;

import com.asg.common.lib.security.util.UserContext;
import com.asg.common.services.dto.*;
import com.asg.common.services.service.impl.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.asg.common.lib.dto.response.ApiResponse.success;


@Slf4j
@RestController
@RequestMapping("/v1/dashboard")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    /**
     * Retrieves a list of pending approvals based on the provided criteria in the request body.
     *
     * @param dto The dashboard DTO containing group, company, and user information
     * @return ResponseEntity containing the list of pending approvals or an error message
     *
     * @apiNote This endpoint requires valid authentication and proper authorization
     *
     * @throws MethodArgumentNotValidException if request validation fails
     */
    @Operation(
            summary = "Get pending approvals",
            description = "Retrieves a list of pending approval items based on the provided criteria in the request body",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved pending approvals",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PendingApprovalsDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input parameters",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            },
            tags = {"Dashboard"},
            security = @SecurityRequirement(name = "bearerAuth")
    )


    @PostMapping("/pending-approvals")
    public ResponseEntity<?> getPendingApprovals() {
        try {
            PendingApprovalResponse pendingApprovals = dashboardService.getDashboardEntity();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("PendingApprovals", pendingApprovals.getPendingApprovals());
            responseData.put("totalElements", pendingApprovals.getTotalElements());
            responseData.put("totalPages",pendingApprovals.getTotalPages());
            responseData.put("pageNumber", pendingApprovals.getPageNumber());
            responseData.put("pageSize", pendingApprovals.getPageSize());
            return success("Pending approvals retrieved successfully", responseData);
        } catch (Exception e) {
            log.error("Error while fetching pending approvals", e);
            throw new RuntimeException("Error retrieving pending approvals: " + e.getMessage(), e);
        }
    }

    @Operation(
            summary = "Get user submit status",
            description = "Retrieves user submission status information based on the provided criteria",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user submit status",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input parameters",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            },
            tags = {"Dashboard"}
    )
    @GetMapping("/submit-status")
    public List<UserApprovalSubmitStatusDto> getUserApprovalSubmitStatus(
            @Parameter(description = "Status filter: ALL/APPROVED/PENDING", example = "ALL")
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @Parameter(description = "Start date for filtering submissions", example = "2025-11-01")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @Parameter(description = "End date for filtering submissions", example = "2025-11-19")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) {
        return dashboardService.fetchUserSubmitStatus(
                String.valueOf(UserContext.getUserPoid()),
                status,
                fromDate,
                toDate
        );
    }

    @Operation(
            summary = "Get recent documents",
            description = "Retrieves list of recent documents based on the provided criteria",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved recent documents",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input parameters",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            },
            tags = {"Dashboard"}
    )

    @GetMapping("/recent-documents")
    public List<RecentDocumentDto> getRecentDocuments() {
        return dashboardService.fetchRecentDocuments(
                UserContext.getUserId(), UserContext.getUserPoid());
    }

    @Operation(
            summary = "Get recent transactions",
            description = "Retrieves current week's recent transactions for the logged-in user, sorted by latest, limited to top 15",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved recent transactions",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            },
            tags = {"Dashboard"}
    )
    @GetMapping("/recent-transactions")
    public ResponseEntity<?> getRecentTransactions() {
        try {
            List<RecentDocumentDto> result = dashboardService.fetchRecentTransactions(
                    UserContext.getUserId(), UserContext.getUserPoid());
            return success("Recent transactions retrieved successfully", result);
        } catch (Exception e) {
            log.error("Error while fetching recent transactions", e);
            throw new RuntimeException("Error retrieving recent transactions: " + e.getMessage(), e);
        }
    }

    @Operation(
            description = "Retrieves user's favorite menu items based on the provided criteria",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved favorite menu",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input parameters",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            },
            tags = {"Dashboard"}
    )

    @GetMapping("/favorite-menu")
    public List<FavoriteMenuDto> getFavoriteMenu() {
        return dashboardService.fetchFavoriteMenu(
                UserContext.getUserId(), UserContext.getUserPoid());
    }

    @Operation(
            summary = "Get dashboard approvals",
            description = "Retrieves approval dashboard data with support for Pending and Completed tabs",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved approvals",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input parameters",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            },
            tags = {"Dashboard"}
    )
    @GetMapping("/approvals")
    public ResponseEntity<?> getApprovals(
            @Parameter(description = "Approval status filter: PENDING (Pending tab) / APPROVED (Completed tab) / ALL", required = false, example = "PENDING")
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @Parameter(description = "Start date of the range", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @Parameter(description = "End date of the range", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) {
        List<ApprovalPendingDto> approvals = dashboardService.fetchApprovalPendingList(
                String.valueOf(UserContext.getUserPoid()),
                status,
                fromDate,
                toDate
        );
        return success("Approvals retrieved successfully", approvals);
    }

    @Operation(
            summary = "Get weekly transactions",
            description = "Retrieves weekly transaction data based on the provided criteria",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved weekly transactions",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input parameters",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            },
            tags = {"Dashboard"}
    )

    @GetMapping("/weekly-transactions")
    public List<WeeklyTransactionDto> getWeeklyTransactions() {
        return dashboardService.fetchWeeklyTransactions(
                String.valueOf(UserContext.getUserPoid()));
    }
}