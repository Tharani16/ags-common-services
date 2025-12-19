package com.asg.common.services.repository;


import com.asg.common.services.dto.GlobalTermsInsertRequestDto;
import com.asg.common.services.dto.GlobalTermsResponseDto;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface GlobalTermsConditionRepository {

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

    String createPoFromRfq(
            Long loginGroupPoid,
            Long loginUserPoid,
            Long loginCompanyPoid,
            Long poPoid,
            String supplierPoid,
            String rfqPoid
    );
}
