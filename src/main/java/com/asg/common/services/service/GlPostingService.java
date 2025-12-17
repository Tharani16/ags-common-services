package com.asg.common.services.service;

import com.asg.common.services.dto.GlPostingViewResponseDto;

import java.sql.SQLException;

public interface GlPostingService {
    GlPostingViewResponseDto fetchGlPostings(String docId, Long transactionPoid) throws SQLException;
    String glreposting (int loginGroupPoid, int loginCompanyPoid, int loginUserPoid, String docId, int transactionPoid, int docRef);
}
