package com.asg.common.services.repository;

import com.asg.common.lib.dto.GlobalTermsDto;
import com.asg.common.lib.dto.request.GlobalTermsInsertRequestDto;
import com.asg.common.lib.dto.response.GlobalTermsResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class GlobalTermsConditionRepositoryImpl implements GlobalTermsConditionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void insertGlobalTerms(List<GlobalTermsInsertRequestDto> requestList) {

        if (requestList == null || requestList.isEmpty()) return;

        for (GlobalTermsInsertRequestDto dto : requestList) {
            try {
                StoredProcedureQuery query = entityManager
                        .createStoredProcedureQuery("PROC_GLOB_TERMS_INSERT");

                query.registerStoredProcedureParameter("P_GROUP_POID", Long.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_COMPANY_POID", Long.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_DOC_ID", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_DOC_KEY_POID", Long.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_LOGIN_USER_POID", Long.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_TERMS_POID", Long.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_DET_ROW_ID", Long.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_ROW_SEQ_NO", Long.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_CLAUSE_NO", String.class, ParameterMode.IN);
                query.registerStoredProcedureParameter("P_CLAUSE_DETAILS", String.class, ParameterMode.IN);

                query.setParameter("P_GROUP_POID", dto.getGroupPoid());
                query.setParameter("P_COMPANY_POID", dto.getCompanyPoid());
                query.setParameter("P_DOC_ID", dto.getDocId());
                query.setParameter("P_DOC_KEY_POID", dto.getDocKeyPoid());
                query.setParameter("P_LOGIN_USER_POID", dto.getLoginUserPoid());
                query.setParameter("P_TERMS_POID", dto.getTermsPoid());
                query.setParameter("P_DET_ROW_ID", dto.getDetRowId());
                query.setParameter("P_ROW_SEQ_NO", dto.getRowSeqNo());
                query.setParameter("P_CLAUSE_NO", dto.getClauseNo());
                query.setParameter("P_CLAUSE_DETAILS", dto.getClauseDetails());

                query.execute();
            } catch (Exception e) {
                log.error("Error inserting global terms for DOC_ID {} and DOC_KEY_POID {}: {}",
                        dto.getDocId(), dto.getDocKeyPoid(), e.getMessage());
                throw new RuntimeException("Failed to insert global terms", e);
            }
        }
    }


    @Override
    public void deleteGlobalTerms(
            Long groupPoid,
            Long companyPoid,
            String documentId,
            Long docKeyPoid,
            Long userPoid
    ) {
        try {
            StoredProcedureQuery query =
                    entityManager.createStoredProcedureQuery("PROC_GLOB_TERMS_DELETE");

            // Register procedure parameters
            query.registerStoredProcedureParameter("P_GROUP_POID", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_COMPANY_POID", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_DOC_ID", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_DOC_KEY_POID", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_LOGIN_USER_POID", Long.class, ParameterMode.IN);

            // Set parameter values
            query.setParameter("P_GROUP_POID", groupPoid);
            query.setParameter("P_COMPANY_POID", companyPoid);
            query.setParameter("P_DOC_ID", documentId);
            query.setParameter("P_DOC_KEY_POID", docKeyPoid);
            query.setParameter("P_LOGIN_USER_POID", userPoid);

            query.execute();

            log.info("PROC_GLOB_TERMS_DELETE executed successfully for DOC_ID={} and DOC_KEY_POID={}",
                    documentId, docKeyPoid);

        } catch (Exception e) {
            log.error("Error executing PROC_GLOB_TERMS_DELETE: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete Global Terms: " + e.getMessage(), e);
        }
    }

    @Override
    public GlobalTermsResponseDto loadGlobalTermsList(
            Long groupPoid,
            Long companyPoid,
            String docId,
            Long docKeyPoid,
            Long termsPoid) {

        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("PROC_GLOB_TERMS_LOADLIST");

        // Register parameters
        query.registerStoredProcedureParameter("P_GROUP_POID", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_COMPANY_POID", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_DOC_ID", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_DOC_KEY_POID", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("P_TERMS_POID", Long.class, ParameterMode.IN);

        query.registerStoredProcedureParameter("OUTDATA", ResultSet.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("P_STATUS", String.class, ParameterMode.OUT);

        // Set parameters
        query.setParameter("P_GROUP_POID", groupPoid);
        query.setParameter("P_COMPANY_POID", companyPoid);
        query.setParameter("P_DOC_ID", docId);
        query.setParameter("P_DOC_KEY_POID", docKeyPoid);
        query.setParameter("P_TERMS_POID", termsPoid);

        // Execute
        query.execute();

        ResultSet rs = (ResultSet) query.getOutputParameterValue("OUTDATA");
        String status = (String) query.getOutputParameterValue("P_STATUS");

        List<GlobalTermsDto> termsList = mapToGlobalTerms(rs);

        GlobalTermsResponseDto response = new GlobalTermsResponseDto();
        response.setTermsList(termsList);


        return response;
    }

    private List<GlobalTermsDto> mapToGlobalTerms(ResultSet rs) {
        List<GlobalTermsDto> list = new ArrayList<>();

        try {
            while (rs.next()) {
                GlobalTermsDto dto = new GlobalTermsDto();
                dto.setRefTermsPoid(rs.getLong("TERMS_POID"));
                dto.setDetRowId(rs.getLong("DET_ROW_ID"));
                dto.setClauseNo(rs.getString("CLAUSE_NO"));
                dto.setClauseDetails(rs.getString("CLAUSE_DETAILS"));
                list.add(dto);
            }
        } catch (SQLException e) {
            log.error("Error mapping ResultSet for PROC_GLOB_TERMS_LOADLIST", e);
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public String createPoFromRfq(
            Long loginGroupPoid,
            Long loginUserPoid,
            Long loginCompanyPoid,
            Long poPoid,
            String supplierPoid,
            String rfqPoid) {

        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("PROC_AP_PO_CREATE_FROM_RFQ");

            // Register IN parameters
            query.registerStoredProcedureParameter("P_LOGIN_GROUP_POID", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_LOGIN_USER_POID", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_LOGIN_COMPANY_POID", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_PO_POID", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_SUPPLIER_POID", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("P_RFQ_POID", String.class, ParameterMode.IN);

            // OUT parameter
            query.registerStoredProcedureParameter("P_RESULT", String.class, ParameterMode.OUT);

            // Setting parameters
            query.setParameter("P_LOGIN_GROUP_POID", loginGroupPoid);
            query.setParameter("P_LOGIN_USER_POID", loginUserPoid);
            query.setParameter("P_LOGIN_COMPANY_POID", loginCompanyPoid);
            query.setParameter("P_PO_POID", poPoid);
            query.setParameter("P_SUPPLIER_POID", supplierPoid);
            query.setParameter("P_RFQ_POID", rfqPoid);

            // Execute
            query.execute();

            // Read OUT result
            String result = (String) query.getOutputParameterValue("P_RESULT");

            log.info("PROC_AP_PO_CREATE_FROM_RFQ executed successfully. Result: {}", result);

            return result;

        } catch (Exception e) {
            log.error("Error executing PROC_AP_PO_CREATE_FROM_RFQ: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create PO from RFQ: " + e.getMessage(), e);
        }
    }
}
