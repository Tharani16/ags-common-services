package com.asg.common.services.controller;

import com.asg.common.services.dto.DraftResponse;
import com.asg.common.services.dto.SaveDraftRequest;
import com.asg.common.services.dto.SaveDraftResponse;
import com.asg.common.services.entity.Draft;
import com.asg.common.services.service.DraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.asg.common.lib.dto.response.ApiResponse.badRequest;
import static com.asg.common.lib.dto.response.ApiResponse.success;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/draft")
public class DraftController {

    private final DraftService service;

    @Operation(
            summary = "Create or Update Draft",
            description = "Create a new draft or update an existing one based on the request payload."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,

            description = """
                    ### Request Body
                        Save a draft document by providing the required details.
                        - **docId:** Unique identifier of the document. This field is mandatory.
                        - **companyId:** ID of the company associated with the draft. Required.
                        - **userPoid:** ID of the user saving the draft. Required.
                        - **jsonData:** JSON structure containing the draft content. Must not be null.
                    
                    ### Notes
                        - All fields are required for successful draft saving.
                        - Ensure `jsonData` follows the expected schema for proper processing.
                    """,

            content = @Content(
                    schema = @Schema(implementation = Draft.class),
                    examples = {
                            @ExampleObject(
                                    name = "Draft Create/Update Example",
                                    value = """
                                            {
                                              "docId": "000-123",
                                              "companyPoid": 2002,
                                              "userPoid": 502,
                                              "jsonData": {
                                                "field1": "value1",
                                                "field2": "value2",
                                                "field3": "value3"
                                              }
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping("/save")
    public ResponseEntity<?> saveDraft(@Valid @RequestBody SaveDraftRequest request) {
        Draft saved = service.saveOrUpdateDraft(request);
        SaveDraftResponse resp = new SaveDraftResponse();
        resp.setDocId(saved.getDocId());
        resp.setCompanyPoid(saved.getCompanyPoid());
        resp.setUserId(saved.getUserPoid());
        return success("Draft saved successfully", resp);
    }

    @Operation(
            summary = "Get Last Task",
            description = """
                        Fetch the draft created for a given companyId, userPoid and docId.
                    
                        ### Request Parameters
                        - **userPoid:** User's Primary Key
                        - **companyPoid:** Company's Primary Key
                        - **docId:** Document's Primary Key
                    """
    )
    @GetMapping("/{companyPoid}/{userPoid}/{docId}")
    public ResponseEntity<?> getDraft(
            @PathVariable @NotBlank String docId,
            @PathVariable @NotNull Long companyPoid,
            @PathVariable @NotNull Long userPoid) {

        if (StringUtils.isBlank(docId)) {
            return badRequest("docId is required");
        }
        if (companyPoid == null) {
            return badRequest("companyId is required");
        }
        if (userPoid == null) {
            return badRequest("userPoid is required");
        }

        Optional<DraftResponse> optionalDraftResponse = service.getDraft(docId, companyPoid, userPoid)
                .map(d -> {
                    DraftResponse resp = new DraftResponse();
                    resp.setDocId(d.getDocId());
                    resp.setCompanyPoid(d.getCompanyPoid());
                    resp.setUserPoid(d.getUserPoid());
                    resp.setJsonData(service.deserialize(d.getDraftData()));
                    resp.setLastModifiedDate(d.getLastModifiedDate() == null ? null : d.getLastModifiedDate().toString());
                    return resp;
                });
        if (optionalDraftResponse.isPresent()) {
            return success("Draft fetched successfully", optionalDraftResponse.get());
        }
        String message = String.format("No draft found for docId=%s, companyId=%s, userId=%s", docId, companyPoid, userPoid);
        log.info(message);
        return success(message, null);
    }

    @Operation(
            summary = "Delete Draft",
            description = """
                    Deletes a draft based on the provided parameters.
                    
                        ### Request Parameters
                        - **userPoid:** User's Primary Key
                        - **companyPoid:** Company's Primary Key
                        - **docId:** Document's Primary Key
                    """
    )
    @DeleteMapping("/{companyId}/{userPoid}/{docId}")
    public ResponseEntity<?> deleteDraft(
            @PathVariable @NotBlank String docId,
            @PathVariable @NotNull Long companyId,
            @PathVariable @NotNull Long userPoid) {

        if (StringUtils.isBlank(docId)) {
            return badRequest("docId is required");
        }
        if (companyId == null) {
            return badRequest("companyId is required");
        }
        if (userPoid == null) {
            return badRequest("userPoid is required");
        }

        boolean deleted = service.deleteDraft(docId, companyId, userPoid);

        if (deleted) {
            String message = String.format("Draft deleted successfully for docId=%s, companyId=%s, userId=%s", docId, companyId, userPoid);
            return success(message, null);
        } else {
            String message = String.format("No draft found to delete for docId=%s, companyId=%s, userId=%s", docId, companyId, userPoid);
            log.info(message);
            return success(message, null);
        }
    }
}
