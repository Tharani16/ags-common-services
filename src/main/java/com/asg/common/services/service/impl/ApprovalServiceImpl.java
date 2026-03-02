package com.asg.common.services.service.impl;

import com.asg.common.lib.security.util.UserContext;
import com.asg.common.services.dto.ApprovalActionRequest;
import com.asg.common.services.dto.ApprovalActionResponse;
import com.asg.common.services.dto.ApprovalLogResponse;
import com.asg.common.services.dto.ApprovalStatusResponse;
import com.asg.common.services.repository.ApprovalRepository;
import com.asg.common.services.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;

    @Override
    public ApprovalActionResponse executeApprovalAction(ApprovalActionRequest request) {
        log.info("Executing approval action: {} for docId: {}, docKeyPoid: {}", 
                request.getApprovalAction(), request.getDocId(), request.getDocKeyPoid());
        
        if (request.getLoginGroupPoid() == null) {
            request.setLoginGroupPoid(UserContext.getGroupPoid());
        }
        if (request.getCompanyPoid() == null) {
            request.setCompanyPoid(UserContext.getCompanyPoid());
        }
        if (request.getUserPoid() == null) {
            request.setUserPoid(UserContext.getUserPoid());
        }
        
        return approvalRepository.executeApprovalAction(request);
    }

    @Override
    public ApprovalStatusResponse getApprovalStatus(String docId, Long docKeyPoid) {
        log.info("Getting approval status for docId: {}, docKeyPoid: {}", docId, docKeyPoid);
        
        Long groupPoid = UserContext.getGroupPoid();
        Long companyPoid = UserContext.getCompanyPoid();
        Long userPoid = UserContext.getUserPoid();
        
        return approvalRepository.getApprovalStatus(groupPoid, companyPoid, userPoid, docId, docKeyPoid);
    }

    @Override
    public ApprovalLogResponse getApprovalLog(String docId, Long docKeyPoid) {
        log.info("Getting approval log for docId: {}, docKeyPoid: {}", docId, docKeyPoid);
        
        Long groupPoid = UserContext.getGroupPoid();
        Long companyPoid = UserContext.getCompanyPoid();
        
        return approvalRepository.getApprovalLog(groupPoid, companyPoid, docId, docKeyPoid);
    }
}
