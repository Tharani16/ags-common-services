package com.asg.common.services.service;

import com.asg.common.services.dto.SaveDraftRequest;
import com.asg.common.services.entity.Draft;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public interface DraftService {
    Draft saveOrUpdateDraft(SaveDraftRequest req);

    Optional<Draft> getDraft(String docId, Long companyId, Long userPoid);

    boolean deleteDraft(String docId, Long companyId, Long userId);

    JsonNode deserialize(String json);

    int clearAllDraftsByUser(Long userPoid);

}
