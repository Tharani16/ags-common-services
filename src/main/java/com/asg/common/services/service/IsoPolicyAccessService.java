package com.asg.common.services.service;

import com.asg.common.services.dto.IsoPolicyAccessRequestDto;
import com.asg.common.services.dto.IsoPolicyAccessResponseDto;
import com.asg.common.services.dto.IsoPolicyDocumentFolderDto;

import java.util.List;

/**
 * Employee-facing side of ISO Documents and Policies (asg-admin-iso, DocId 700-101): the Home page
 * Documents widget and the Access Log it writes.
 * <p>
 * This lives in common-services rather than with the rest of the ISO module because the caller is an
 * employee opening a published document: they hold no rights on document 700-101, so neither endpoint
 * can be gated by {@code @AllowedAction}. Both act as the logged-in employee, resolved from the token
 * via {@code HR_EMPLOYEE_MASTER.LOGIN_USER_POID}. The ISO admin screen itself stays in asg-admin-iso.
 */
public interface IsoPolicyAccessService {

    /**
     * The ISO documents and policies published to the logged-in employee, grouped into Category
     * folders, each document carrying its attachments. Expired documents are excluded unless
     * {@code includeExpired}.
     */
    List<IsoPolicyDocumentFolderDto> getMyDocuments(boolean includeExpired);

    /**
     * Records that the logged-in employee opened a document, and optionally that they acknowledged
     * it. Upserts their single Access Log row: the first call creates it, later calls bump
     * LAST_ACCESSED_TIME and ACCESS_COUNT.
     */
    IsoPolicyAccessResponseDto recordAccess(Long transactionPoid, IsoPolicyAccessRequestDto request);
}
