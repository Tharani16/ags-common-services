package com.asg.common.services.service;


import com.asg.common.services.dto.DocAcquireLockRequestDto;
import com.asg.common.lib.dto.request.DocReleaseLockRequestDto;
import com.asg.common.services.dto.DocUpdateLockRequestDto;

public interface DocumentLockService {

    String acquireLock(DocAcquireLockRequestDto request);

    String releaseLock(DocReleaseLockRequestDto request);

    String updateLock(DocUpdateLockRequestDto request);
}
