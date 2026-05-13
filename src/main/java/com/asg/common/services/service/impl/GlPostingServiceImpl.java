package com.asg.common.services.service.impl;

import com.asg.common.lib.entity.DocumentEntity;
import com.asg.common.lib.dto.response.GlPostingViewResponseDto;
import com.asg.common.lib.service.ApprovalService;
import com.asg.common.services.repository.GlPostingRepository;
import com.asg.common.services.service.GlPostingService;
import com.asg.common.lib.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class GlPostingServiceImpl implements GlPostingService {

    private final GlPostingRepository repository;
    private final ApprovalService approvalService;
    private final DocumentRepository documentRepository;

    @Override
    public GlPostingViewResponseDto fetchGlPostings(String docId, Long transactionPoid) throws SQLException {
        GlPostingViewResponseDto glPostingViewResponseDto = repository.getGlPostings( docId, transactionPoid);
        return glPostingViewResponseDto;
    }

    @Override
    public String glreposting(int loginGroupPoid, int loginCompanyPoid, int loginUserPoid, String docId, int transactionPoid, String docRef) {
        DocumentEntity document = documentRepository.findByDocId(docId);
        if (document == null) {
            return "Document not found for docId: " + docId;
        }

        String docName = document.getDocName() != null ? document.getDocName() : docId;
        if (!"Y".equalsIgnoreCase(document.getGlPosting())) {
            return "GL posting is not enabled for this document " + docName;
        }

        if ("Y".equalsIgnoreCase(document.getApprovalRequired())) {
            String approvalStatus = approvalService.getApprovalStatus(docId, transactionPoid);
            if (approvalStatus.startsWith("ERROR") ||
                (!"APPROVED".equals(approvalStatus) && !"FINAL_APPROVAL_COMPLETED".equals(approvalStatus))) {
                return "Document must be approved before GL re-posting can be performed. Status: " + approvalStatus;
            }
        }

        return repository.glreposting(loginGroupPoid, loginCompanyPoid, loginUserPoid, docId, transactionPoid, docRef);
    }
}
