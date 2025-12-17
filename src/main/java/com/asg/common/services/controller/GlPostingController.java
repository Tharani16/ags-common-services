package com.asg.common.services.controller;

import com.asg.common.services.dto.GlPostingViewResponseDto;
import com.asg.common.services.dto.GlrePostingDto;
import com.asg.common.services.service.GlPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

import static com.asg.common.lib.dto.response.ApiResponse.success;

@RestController
@RequestMapping("/v1/gl-postings")
@RequiredArgsConstructor
@Slf4j
public class GlPostingController {

    private final GlPostingService service;

    @Operation(
            summary = "Retrieve GL postings for a document",
            description = """
                    Fetches comprehensive general ledger posting details including:
                    - Ledger Entries: Main GL posting entries with debit/credit amounts
                    - Billwise Breakup: Bill-wise breakdown of transactions
                    - Cost Breakup: Cost center allocation details
                    - VAT Breakup: Tax breakdown information
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved GL postings with all breakup details",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GlPostingViewResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters provided"),
            @ApiResponse(responseCode = "404", description = "No GL postings found for the given criteria"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> getGlPostings(

            @Parameter(description = "Document ID", required = true)
            @RequestParam String docId,
            @Parameter(description = "Transaction POID", required = true)
            @RequestParam Long transactionPoid) throws SQLException {
        GlPostingViewResponseDto glPostings = service.fetchGlPostings(docId, transactionPoid);
        return success("GL postings retrieved successfully", glPostings);
    }

    @Operation(
            summary = "Perform GL re-posting",
            description = "Re-posts general ledger entries for a specific transaction"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "GL re-posting completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error during posting")
    })
    @PostMapping
    public ResponseEntity<?> glrePosting(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "GL re-posting request details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GlrePostingDto.class))
            )
            @RequestBody GlrePostingDto glrePostingDto) {
        String message = service.glreposting(
                glrePostingDto.getLoginGroupPoid(),
                glrePostingDto.getLoginCompanyPoid(),
                glrePostingDto.getLoginUserPoid(),
                glrePostingDto.getDocId(),
                glrePostingDto.getTransactionPoid(),
                glrePostingDto.getDocRef()
        );
        return success("GL posting completed successfully", message);
    }

}
