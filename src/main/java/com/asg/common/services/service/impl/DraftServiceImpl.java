package com.asg.common.services.service.impl;


import com.asg.common.services.dto.SaveDraftRequest;
import com.asg.common.services.entity.Draft;
import com.asg.common.services.repository.DraftRepository;
import com.asg.common.services.service.DraftService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {

    private final DraftRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Draft saveOrUpdateDraft(SaveDraftRequest req) {
        log.info("saveOrUpdateDraft : docId={}, companyPoid={}, userPoid={}", req.getDocId(), req.getCompanyPoid(), req.getUserPoid());
        String payload = serialize(req.getJsonData());

        Optional<Draft> draftOptional = repository.findByDocIdAndCompanyPoidAndUserPoid(req.getDocId(), req.getCompanyPoid(), req.getUserPoid());

        if (draftOptional.isPresent()) {
            Draft d = draftOptional.get();
            d.setDraftData(payload);
            log.debug("Updating draft for docId={}, companyId={}, userId={}", d.getDocId(), d.getCompanyPoid(), d.getUserPoid());
            return repository.save(d);
        }

        Draft d = new Draft();
        d.setDocId(req.getDocId());
        d.setCompanyPoid(req.getCompanyPoid());
        d.setUserPoid(req.getUserPoid());
        d.setDraftData(payload);

        try {
            log.debug("Inserting draft for docId={}, companyId={}, userId={}", d.getDocId(), d.getCompanyPoid(), d.getUserPoid());
            return repository.saveAndFlush(d);
        } catch (DataIntegrityViolationException race) {
            log.warn("Race on upsert detected; retrying update for docId={}, companyId={}, userId={}", d.getDocId(), d.getCompanyPoid(), d.getUserPoid());
            Draft retry = repository.findByDocIdAndCompanyPoidAndUserPoid(d.getDocId(), d.getCompanyPoid(), d.getUserPoid()).orElseThrow(() -> race);
            retry.setDraftData(payload);
            return repository.save(retry);
        }
    }

    @Transactional()
    public Optional<Draft> getDraft(String docId, Long companyId, Long userId) {
        log.info("getDraft : docId={}, companyId={}, userId={}", docId, companyId, userId);
        return repository.findByDocIdAndCompanyPoidAndUserPoid(docId, companyId, userId);
    }

    @Transactional
    public boolean deleteDraft(String docId, Long companyId, Long userId) {
        Optional<Draft> draft = repository.findByDocIdAndCompanyPoidAndUserPoid(docId, companyId, userId);
        draft.ifPresent(repository::delete);
        log.debug("Deleted draft for docId={}, companyId={}, userId={}", docId, companyId, userId);

        return draft.isPresent();
    }

    private String serialize(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new IllegalArgumentException("Json Data is not valid JSON", e);
        }
    }

    public JsonNode deserialize(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse stored draft JSON", e);
        }
    }

    @Transactional
    public int clearAllDraftsByUser(Long userPoid) {
        long count = repository.deleteByUserPoid(userPoid);
        log.debug("Cleared {} drafts for userPoid={}", count, userPoid);
        return (int) count;
    }

}
