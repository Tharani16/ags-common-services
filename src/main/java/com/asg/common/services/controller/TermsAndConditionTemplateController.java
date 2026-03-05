package com.asg.common.services.controller;

import com.asg.common.lib.dto.LovGetListDto;
import com.asg.common.services.service.TermsAndConditionTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.asg.common.lib.dto.response.ApiResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/terms-template")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Terms and Template", description = "APIs for managing terms and templates")
public class TermsAndConditionTemplateController {

    private final TermsAndConditionTemplateService termsAndConditionTemplateServiceImpl;

    @Operation(summary = "Get parameter value by name", description = "Retrieve parameter value from GLOBAL_PARAMETERS table by parameter name")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Parameter value retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parameter not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/default-value/{parameterName}/{lovName}")
    public ResponseEntity<?> getParameterByName(@Parameter(description = "Parameter name to search", required = true)
                                                @PathVariable String parameterName,
                                                @Parameter(description = "Lov name to fetch", required = true)
                                                @PathVariable String lovName) {
        LovGetListDto result = termsAndConditionTemplateServiceImpl.getTermsAndConditionTemplateByParameterName(parameterName, lovName);
        return success("Default terms retrieved successfully", result);
    }
}
