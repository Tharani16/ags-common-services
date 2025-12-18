package com.asg.common.services.controller;

import com.asg.common.lib.annotation.AllowedAction;
import com.asg.common.lib.enums.UserRolesRightsEnum;
import com.asg.common.lib.security.util.UserContext;
import com.asg.common.services.dto.*;
import com.asg.common.services.service.CommonDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.asg.common.lib.dto.response.ApiResponse.success;

@RestController
@RequestMapping("/v1/common")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CommonDataServiceController {

    private final CommonDataService glMasterService;

    @Operation(
            summary = "Fetch GL Master Details",
            description = "Fetches GL Master details based on the provided GL POID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "GL Master details fetched successfully",
                            content = @Content(schema = @Schema(implementation = GLMasterCommonDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "GL Master record not found")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/glDetails/{glPoid}")
    public ResponseEntity<?> getGLMasterData(
            @Parameter(description = "glPoid reference identifier", required = true, example = "201")
            @PathVariable Long glPoid) {
        GLMasterCommonDTO response = glMasterService.getGLMasterDetails(glPoid);
        return success("GL Master Data fetched successfully", response);
    }

    @Operation(
            summary = "Calculate Tax for Petty Cash",
            description = "Calculates tax for a given petty cash amount based on the provided Tax POID and debit amount.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tax calculated successfully",
                            content = @Content(schema = @Schema(implementation = TaxCalculationResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Tax record not found")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/calculateTax")
    public ResponseEntity<?> calculateTax(
            @Parameter(description = "Tax POID reference identifier", required = true, example = "201")
            @RequestParam Long taxPoid,

            @Parameter(description = "Debit amount for which tax is to be calculated", required = true, example = "1000.0")
            @RequestParam Double drAmt) {

        TaxCalculationResponseDto response =
                glMasterService.calculateTaxForPettyCash(taxPoid, drAmt);

        return  success("Tax Calculation fetched successfully", response);
    }

    @Operation(
            summary = "Insert Global Terms",
            description = "Inserts one or more global terms records for a given document.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Global terms inserted successfully",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or missing input parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/term-condition/insert")
    public ResponseEntity<?> insertGlobalTerms(
            @RequestBody List<GlobalTermsInsertRequestDto> requestList

    ) {
        glMasterService.insertGlobalTerms(requestList);
        return success("Global Terms inserted successfully", null);
    }


    @Operation(
            summary = "Delete Global Terms",
            description = "Deletes global terms for a specific document and key.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Global terms deleted successfully",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @DeleteMapping("/term-condition/delete")
    public ResponseEntity<?> deleteGlobalTerms(
            @Parameter(description = "Document ID", required = true, example = "PO-1001")
            @RequestParam String documentId,

            @Parameter(description = "Document Key POID", required = true, example = "2001")
            @RequestParam Long docKeyPoid
    ) {

        glMasterService.deleteGlobalTerms(UserContext.getGroupPoid(), UserContext.getCompanyPoid(), documentId, docKeyPoid, UserContext.getUserPoid());
        return success("Global Terms deleted successfully", null);
    }

    @Operation(
            summary = "Load Global Terms List",
            description = "Loads global terms list for the specified document and document key.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Global Terms list fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GlobalTermsResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/term-condition/load")
    public ResponseEntity<?> loadGlobalTermsList(
            @Parameter(description = "Document ID", required = true, example = "PO-1001")
            @RequestParam String documentId,

            @Parameter(description = "Document Key POID", required = true, example = "2001")
            @RequestParam Long docKeyPoid,

            @Parameter(description = "Terms POID (optional filter)", required = false, example = "10")
            @RequestParam(required = false) Long termsPoid
    ) {

        GlobalTermsResponseDto response =
                glMasterService.loadGlobalTermsList(UserContext.getGroupPoid(), UserContext.getCompanyPoid(), documentId, docKeyPoid, termsPoid);

        return success("Global Terms list fetched successfully", response);
    }

    @Operation(
            summary = "Get Currency Rate",
            description = "Fetches the standard or custom currency rate using Oracle procedure PROC_GLOB_CURRENCY_GETRATE.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Currency rate fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CurrencyRateResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/currency/get-rate")
    public ResponseEntity<?> getCurrencyRate(

            @Parameter(description = "Document ID (optional)", required = false, example = "300-105")
            @RequestParam(required = false, defaultValue = "#{null}") String docId,

            @Parameter(description = "Document Key POID (optional)", required = false, example = "235030")
            @RequestParam(required = false, defaultValue = "#{null}") Long docKeyPoid,

            @Parameter(description = "Currency Code (required)", required = true, example = "USD")
            @RequestParam String currencyCode,

            @Parameter(description = "Optional custom filter parameters", required = false, example = "CUSTOM_FILTER")
            @RequestParam(required = false, defaultValue = "#{null}") String parameters
    ) {

        Double rate = glMasterService.getCurrencyRate(
                UserContext.getGroupPoid(),
                UserContext.getCompanyPoid(),
                UserContext.getUserPoid(),
                null,
                null,
                currencyCode,
                null
        );

        CurrencyRateResponseDto response = CurrencyRateResponseDto.builder()
                .currencyCode(currencyCode)
                .rate(rate)
                .build();

        return success("Currency rate fetched successfully", response);
    }

    @Operation(
            summary = "Fetch Stock Master Details",
            description = "Fetches stock details along with unit LOV details (STOCK_UNIT) and tax LOV details (INPUT_TAX_MASTER).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock details fetched successfully",
                            content = @Content(schema = @Schema(implementation = StockDetailsResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid stockPoid provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Stock record not found")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/stock/details/{stockPoid}")
    public ResponseEntity<?> getStockDetails(
            @Parameter(description = "Stock POID reference", required = true, example = "2001")
            @PathVariable Long stockPoid
    ) {

        StockDetailsResponse response = glMasterService.getStockDetails(stockPoid);

        return success("Stock details fetched successfully", response);
    }

}
