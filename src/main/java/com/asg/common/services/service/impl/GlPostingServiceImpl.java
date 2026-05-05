package com.asg.common.services.service.impl;

import com.asg.common.lib.dto.response.GlPostingViewResponseDto;
import com.asg.common.lib.service.ApprovalService;
import com.asg.common.services.repository.GlPostingRepository;
import com.asg.common.services.service.GlPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class GlPostingServiceImpl implements GlPostingService {

    private final GlPostingRepository repository;
    private final ApprovalService approvalService;

    @Override
    public GlPostingViewResponseDto fetchGlPostings(String docId, Long transactionPoid) throws SQLException {
        GlPostingViewResponseDto glPostingViewResponseDto = repository.getGlPostings( docId, transactionPoid);
        return glPostingViewResponseDto;
    }

    @Override
    public String glreposting(int loginGroupPoid, int loginCompanyPoid, int loginUserPoid, String docId, int transactionPoid, String docRef) {
        String approvalStatus = approvalService.getApprovalStatus(docId, transactionPoid);
        if (approvalStatus.startsWith("ERROR") || 
            (!"APPROVED".equals(approvalStatus) && !"FINAL_APPROVAL_COMPLETED".equals(approvalStatus))) {
            return "Document must be approved before GL re-posting can be performed. Status: " + approvalStatus;
        }
        return repository.glreposting(loginGroupPoid, loginCompanyPoid, loginUserPoid, docId, transactionPoid, docRef);
    }
}
