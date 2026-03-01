package com.asg.common.services.controller;

import com.asg.common.lib.annotation.AllowedAction;
import com.asg.common.lib.enums.UserRolesRightsEnum;
import com.asg.common.services.dto.*;
import com.asg.common.services.enums.ApprovalAction;
import com.asg.common.services.service.ApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.asg.common.lib.dto.response.ApiResponse.*;

@RestController
@RequestMapping("/v1/approval")
@RequiredArgsConstructor
@Slf4j
public class ApprovalController {

    private final ApprovalService approvalService;

    @Operation(
            summary = "Execute approval action",
            description = """
                    Handles all approval workflow actions based on the approvalAction parameter:
                    - SUBMIT_FOR_APPROVAL: Initiates the approval workflow
                    - RECALL_FOR_CHANGE: Withdraws document from approval process
                    - APPROVE: Approves the document
                    - APPROVE_WITH_COMMENTS: Approves with comments
                    - RETURN_FOR_CORRECTION: Sends document back for changes
                    - REJECT: Denies the approval
                    - CANCEL_APPROVAL: Cancels the approval process
                    - SPECIAL_SUBMIT: Submits for special field authorization
                    - SPECIAL_APPROVE: Approves special field changes
                    
                    The stored procedure PROC_GLOB_APPROVAL_ACTION handles multi-level approvals and 
                    calls PROC_GLOB_APPROVAL_CUSTOM for document-specific validations.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Approval action request with document and user details",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "docId": "300-100",
                                              "docKeyPoid": 46327,
                                              "approvalAction": "SUBMIT_FOR_APPROVAL",
                                              "comments": "Please review and approve",
                                              "docName": "Sales Invoice (Ship Chandling",
                                              "docRef": "MTAI2600090"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Approval action executed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                              "success": true,
                                              "message": "Approval action executed successfully",
                                              "statusCode": 200,
                                              "result": {
                                                "data": {
                                                  "status": "SUCCESS",
                                                  "message": "SUCCESS",
                                                  "approverUserPoid": "505",
                                                  "approvalPoid": 789,
                                                  "success": true
                                                }
                                              }
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error during approval action"
                    )
            }
    )
    @PostMapping("/action")
    public ResponseEntity<?> executeApprovalAction(
            @Parameter(description = "Approval action request payload", required = true)
            @Valid @RequestBody ApprovalActionRequest request) {
        
        log.info("Executing approval action: {} for document: {}", 
                request.getApprovalAction(), request.getDocId());
        
        ApprovalActionResponse response = approvalService.executeApprovalAction(request);
        
        if (response.isSuccess()) {
            return success("Approval action executed successfully", response);
        } else {
            return badRequest("Approval action failed: " + response.getMessage());
        }
    }


    @Operation(
            summary = "Get approval status and button states",
            description = """
                    Retrieves the current approval status of a document and determines which approval 
                    buttons should be enabled/disabled based on:
                    - Current approval state
                    - User's role and permissions
                    - Document workflow configuration
                    
                    Returns button states for all approval menu options:
                    Submit for Approval, Recall for Change, Approve, Approve With Comments, 
                    Return for Correction, Reject, Special Submit, Special Approve
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Approval status retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                              "success": true,
                                              "message": "Approval status retrieved successfully",
                                              "statusCode": 200,
                                              "result": {
                                                "data": {
                                                  "statusCode": "SUBMIT_FOR_APPROVAL",
                                                  "statusDetails": "Pending approval from manager",
                                                  "approverUsers": "505,506",
                                                  "color": "Orange",
                                                  "submitForApprovalEnabled": false,
                                                  "recallForChangeEnabled": true,
                                                  "returnForCorrectionEnabled": true,
                                                  "approveEnabled": true,
                                                  "approveWithCommentsEnabled": true,
                                                  "rejectEnabled": true,
                                                  "specialApproveEnabled": false,
                                                  "specialSubmitEnabled": false,
                                                  "approvalMenuVisible": true
                                                }
                                              }
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error while retrieving status"
                    )
            }
    )
    @AllowedAction(UserRolesRightsEnum.VIEW)
    @GetMapping("/status")
    public ResponseEntity<?> getApprovalStatus(
            @Parameter(description = "Document ID", required = true)
            @RequestParam String docId,
            @Parameter(description = "Document key POID", required = true)
            @RequestParam Long docKeyPoid) {
        log.info("Getting approval status for document: {}, docKeyPoid: {}", docId, docKeyPoid);
        ApprovalStatusResponse response = approvalService.getApprovalStatus(docId, docKeyPoid);
        return success("Approval status retrieved successfully", response);
    }

    @Operation(
            summary = "Get all approval actions",
            description = "Returns a list of all available approval actions with their codes and display names",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Approval actions retrieved successfully"
                    )
            }
    )
    @GetMapping("/actions")
    public ResponseEntity<?> getAllApprovalActions() {
        List<ApprovalActionInfo> actions = Arrays.stream(ApprovalAction.values())
                .filter(action -> action != ApprovalAction.STATUS_CHECK)
                .map(action -> ApprovalActionInfo.builder()
                        .code(action.getCode())
                        .displayName(action.getDisplayName())
                        .build())
                .collect(Collectors.toList());
        return success("Approval actions retrieved successfully", actions);
    }

    @Operation(
            summary = "Get approval log",
            description = "Retrieves the approval history/log for a document showing all approval actions taken",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Approval log retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                              "success": true,
                                              "message": "Approval log retrieved successfully",
                                              "statusCode": 200,
                                              "result": {
                                                "data": {
                                                  "logs": [
                                                    {
                                                      "ACTION_TYPE": "SUBMIT_FOR_APPROVAL",
                                                      "ACTIONED_BY": "John Doe",
                                                      "ACTIONED_DATE": "2025-01-15",
                                                      "COMMENTS": "Please review"
                                                    }
                                                  ],
                                                  "columns": [
                                                    {"columnName": "ACTION_TYPE", "columnWidth": "150px"},
                                                    {"columnName": "ACTIONED_BY", "columnWidth": "200px"}
                                                  ]
                                                }
                                              }
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/log")
    public ResponseEntity<?> getApprovalLog(
            @Parameter(description = "Document ID", required = true)
            @RequestParam String docId,
            @Parameter(description = "Document key POID", required = true)
            @RequestParam Long docKeyPoid) {
        log.info("Getting approval log for document: {}, docKeyPoid: {}", docId, docKeyPoid);
        ApprovalLogResponse response = approvalService.getApprovalLog(docId, docKeyPoid);
        return success("Approval log retrieved successfully", response);
    }
}
