package com.asg.common.services.service;

import com.asg.common.services.dto.*;

import java.util.List;

public interface CommonDataService {
    GLMasterCommonDTO getGLMasterDetails(Long glPoid);

    TaxCalculationResponseDto calculateTaxForPettyCash(Long taxPoid, Double drAmt);

    void insertGlobalTerms(List<GlobalTermsInsertRequestDto> requestList);

    void deleteGlobalTerms(
            Long groupPoid,
            Long companyPoid,
            String documentId,
            Long docKeyPoid,
            Long userPoid
    );

    GlobalTermsResponseDto loadGlobalTermsList(
            Long groupPoid,
            Long companyPoid,
            String docId,
            Long docKeyPoid,
            Long termsPoid
    );

    ReconcileResultDto fetchReconDate(String docId,
                                      Long docKeyPoid
    );

    Double getCurrencyRate(
            Long groupPoid,
            Long companyPoid,
            Long userPoid,
            String docId,
            Long docKeyPoid,
            String currencyCode,
            String parameters
    );

    public StockDetailsResponse getStockDetails(Long stockPoid);

}
