package com.asg.common.services.service.impl;

import com.asg.common.services.dto.DocAcquireLockRequestDto;
import com.asg.common.lib.dto.request.DocReleaseLockRequestDto;
import com.asg.common.services.dto.DocUpdateLockRequestDto;
import com.asg.common.services.repository.DocumentLockRepository;
import com.asg.common.services.service.DocumentLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentLockServiceImpl implements DocumentLockService {

    private final DocumentLockRepository documentLockRepository;

    @Autowired
    public DocumentLockServiceImpl(DocumentLockRepository documentLockRepository) {
        this.documentLockRepository = documentLockRepository;
    }

    @Override
    public String acquireLock(DocAcquireLockRequestDto request) {
        return documentLockRepository.acquireLock(request);
    }

    @Override
    public String releaseLock(DocReleaseLockRequestDto request) {
        return documentLockRepository.releaseLock(request);
    }

    @Override
    public String updateLock(DocUpdateLockRequestDto request) {

        return documentLockRepository.updateLock(request);
    }
}
