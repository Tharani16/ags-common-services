package com.asg.common.services.service.impl;

import com.asg.common.lib.dto.GLMasterCommonDTO;
import com.asg.common.lib.dto.GLMasterDto;
import com.asg.common.lib.dto.LovGetListDto;
import com.asg.common.lib.dto.ReconcileResultDto;
import com.asg.common.lib.dto.StockInfoDto;
import com.asg.common.lib.dto.TaxMasterDto;
import com.asg.common.lib.dto.request.GlobalTermsInsertRequestDto;
import com.asg.common.lib.dto.response.GlobalTermsResponseDto;
import com.asg.common.lib.dto.response.StockDetailsResponse;
import com.asg.common.lib.dto.response.TaxCalculationResponseDto;
import com.asg.common.lib.exception.ValidationException;
import com.asg.common.lib.security.util.UserContext;
import com.asg.common.lib.service.LoggingService;
import com.asg.common.lib.service.LovDataService;
import com.asg.common.services.client.TaxServiceClient;
import com.asg.common.services.client.GLMasterServiceClient;
import com.asg.common.services.client.StockServiceClient;
import com.asg.common.services.repository.GlobalTermsConditionRepository;
import com.asg.common.services.service.CommonDataService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonDataServiceImpl implements CommonDataService {

    private final GLMasterServiceClient glMasterServiceClient;
    private final TaxServiceClient taxServiceClient;
    private final StockServiceClient stockServiceClient;
    private final GlobalTermsConditionRepository globalTermsConditionRepository;
    private final LovDataService lovService;
    private final LoggingService loggingService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public GLMasterCommonDTO getGLMasterDetails(Long glPoid) {
        GLMasterDto gl = glMasterServiceClient.getGLMaster(glPoid);

        GLMasterCommonDTO dto = new GLMasterCommonDTO();
        dto.setGlCode(gl.getGlCode());
        dto.setGlDescription(gl.getGlDescription());
        dto.setCostGroup(gl.getCostGroup());
        dto.setBillwise(gl.getBillwise());
        dto.setPrepaymentLedger(gl.getPrepaymentLedger());
        dto.setInterCompanyAc(gl.getInterCompanyAc());
        dto.setControlAcNature(gl.getControlAcNature());
        dto.setGlAcType(gl.getGlAcType());
        return dto;
    }


    private TaxMasterDto getTaxMaster(Long taxPoid) {
        return taxServiceClient.getTaxMaster(taxPoid);
    }

    public TaxCalculationResponseDto calculateTaxForPettyCash(Long taxPoid, Double drAmt) {

        if (taxPoid == null || drAmt == null) {
            throw new RuntimeException("Tax Poid and Dr Amount are required");
        }

        TaxMasterDto taxMaster = getTaxMaster(taxPoid);

        Double taxPercentage = taxMaster.getPercentage();

        Double taxAmt = (drAmt * taxPercentage) / 100.0;
        Double totalAmt = drAmt + taxAmt;

        return TaxCalculationResponseDto.builder()
                .drAmt(drAmt)
                .taxPercentage(taxPercentage)
                .taxAmount(taxAmt)
                .totalAmount(totalAmt)
                .build();
    }

    @Override
    @Transactional
    public void insertGlobalTerms(List<GlobalTermsInsertRequestDto> requestList) {

        if (requestList == null) {
            throw new ValidationException("No Global Terms entries provided");
        }

        // Handle empty list — user deleted all rows (legacy: deleteCustomChanges)
        if (requestList.isEmpty()) {
            return;
        }

        GlobalTermsInsertRequestDto first = requestList.get(0);

        if (first.getTermsPoid() == null || first.getTermsPoid() == 0) {
            throw new ValidationException("No Terms and Conditions Template selected...");
        }

        if (first.getCompanyPoid() == null ||
                first.getDocId() == null ||
                first.getDocKeyPoid() == null ||
                first.getDetRowId() == null) {

            throw new ValidationException(
                    "Missing mandatory fields: groupPoid, companyPoid, docId, docKeyPoid, loginUserPoid, termsPoid"
            );
        }

        globalTermsConditionRepository.insertGlobalTerms(requestList);

        loggingService.createLogSummaryEntry(
                first.getDocId(),
                first.getDocKeyPoid().toString(),
                "Terms and Conditions modified..."
        );
    }

    @Override
    public void deleteGlobalTerms(
            Long groupPoid,
            Long companyPoid,
            String documentId,
            Long docKeyPoid,
            Long userPoid
    ) {
        globalTermsConditionRepository.deleteGlobalTerms(
                groupPoid,
                companyPoid,
                documentId,
                docKeyPoid,
                userPoid
        );
        loggingService.createLogSummaryEntry(documentId, docKeyPoid.toString(),
                "Custom Terms and Conditions deleted...");
    }

    @Override
    public GlobalTermsResponseDto loadGlobalTermsList(
            Long groupPoid,
            Long companyPoid,
            String docId,
            Long docKeyPoid,
            Long termsPoid
    ) {
        return globalTermsConditionRepository.loadGlobalTermsList(
                groupPoid,
                companyPoid,
                docId,
                docKeyPoid,
                termsPoid
        );
    }

    @Override
    public ReconcileResultDto fetchReconDate(
            String docId,
            Long docKeyPoid) {

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("PROC_DEBIT_PAYMENT_RECON_DATE");

        // Register IN parameters
        query.registerStoredProcedureParameter("P_LOGIN_GROUP_POID", Long.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_LOGIN_COMPANY_POID", Long.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_LOGIN_USER_POID", Long.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_DOC_ID", String.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_DOC_KEY_POID", Long.class, jakarta.persistence.ParameterMode.IN);

        // Register OUT cursor
        query.registerStoredProcedureParameter("OUTDATA", void.class, jakarta.persistence.ParameterMode.REF_CURSOR);

        // Set input values
        query.setParameter("P_LOGIN_GROUP_POID", UserContext.getGroupPoid());
        query.setParameter("P_LOGIN_COMPANY_POID", UserContext.getCompanyPoid());
        query.setParameter("P_LOGIN_USER_POID", UserContext.getUserPoid());
        query.setParameter("P_DOC_ID", docId);
        query.setParameter("P_DOC_KEY_POID", docKeyPoid);

        // Execute SP
        query.execute();

        Object cursor = query.getOutputParameterValue("OUTDATA");

        return mapCursorToDto(cursor);
    }

    private ReconcileResultDto mapCursorToDto(Object cursor) {

        try {
            ResultSet rs = (ResultSet) cursor;

            if (rs.next()) {
                return new ReconcileResultDto(
                        rs.getString("RECONCILE_DATE"),
                        rs.getString("HOLD")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading cursor", e);
        }

        return new ReconcileResultDto(null, null);
    }

    @Override
    public Double getCurrencyRate(
            Long groupPoid,
            Long companyPoid,
            Long userPoid,
            String docId,
            Long docKeyPoid,
            String currencyCode,
            String parameters
    ) {

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("PROC_GLOB_CURRENCY_GETRATE");

        query.registerStoredProcedureParameter("P_LOGIN_GROUP_POID", Long.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_LOGIN_COMPANY_POID", Long.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_LOGIN_USER_POID", Long.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_DOC_ID", String.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_DOC_KEY_POID", Long.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_CURRENCY_CODE", String.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("P_PARAMETERS", String.class, jakarta.persistence.ParameterMode.IN);

        query.registerStoredProcedureParameter("P_CURRENCY_RATE", Double.class, jakarta.persistence.ParameterMode.OUT);

        query.setParameter("P_LOGIN_GROUP_POID", groupPoid);
        query.setParameter("P_LOGIN_COMPANY_POID", companyPoid);
        query.setParameter("P_LOGIN_USER_POID", userPoid);
        query.setParameter("P_DOC_ID", docId);
        query.setParameter("P_DOC_KEY_POID", docKeyPoid);
        query.setParameter("P_CURRENCY_CODE", currencyCode);
        query.setParameter("P_PARAMETERS", parameters);

        query.execute();

        Object rate = query.getOutputParameterValue("P_CURRENCY_RATE");

        return (rate != null) ? ((Number) rate).doubleValue() : null;
    }

    @Override
    public StockDetailsResponse getStockDetails(Long stockPoid) {
        StockInfoDto stock = stockServiceClient.getStockInfo(stockPoid);

        LovGetListDto stockLov = lovService.getDetailsByPoidAndLovName(
                stock.getStockPoid(),
                "STOCK_MASTER"
        );

        LovGetListDto unitLov = lovService.getDetailsByPoidAndLovName(
                stock.getStockUnitPoid(),
                "STOCK_UNIT"
        );

        LovGetListDto taxLov = lovService.getDetailsByPoidAndLovName(
                stock.getTaxPoid(),
                "INPUT_TAX_MASTER"
        );

        return StockDetailsResponse.builder()
                .stockPoid(stock.getStockPoid())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .stockName2(stock.getStockName2())
                .stockDtl(stockLov)
                .stockUnitPoid(stock.getStockUnitPoid())
                .unitDtl(unitLov)
                .inputTaxPoid(stock.getTaxPoid())
                .taxDtl(taxLov)
                .stockCost(stock.getStockCost())
                .remarks(null)
                .build();
    }

    @Override
    public String createPoFromRfq(
            Long loginGroupPoid,
            Long loginUserPoid,
            Long loginCompanyPoid,
            Long poPoid,
            String supplierPoid,
            String rfqPoid
    ) {
        return globalTermsConditionRepository.createPoFromRfq(
                loginGroupPoid,
                loginUserPoid,
                loginCompanyPoid,
                poPoid,
                supplierPoid,
                rfqPoid
        );
    }

}
