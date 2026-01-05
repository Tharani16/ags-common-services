package com.asg.common.services.service;

import com.asg.common.services.dto.SaveDraftRequest;
import com.asg.common.services.entity.Draft;
import com.asg.common.services.repository.DraftRepository;
import com.asg.common.services.service.impl.DraftServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Draft Service Implementation Tests")
class DraftServiceImplTest {

    @Mock
    private DraftRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DraftServiceImpl service;

    private SaveDraftRequest validRequest;
    private Draft existingDraft;
    private JsonNode jsonNode;

    @BeforeEach
    void setUp() throws Exception {
        validRequest = new SaveDraftRequest();
        validRequest.setDocId("DOC001");
        validRequest.setCompanyPoid(1001L);
        validRequest.setUserPoid(2001L);

        jsonNode = mock(JsonNode.class);
        validRequest.setJsonData(jsonNode);

        existingDraft = new Draft();
        existingDraft.setDocId("DOC001");
        existingDraft.setCompanyPoid(1001L);
        existingDraft.setUserPoid(2001L);
        existingDraft.setDraftData("{\"field1\":\"value1\"}");
    }

    @Nested
    @DisplayName("Save or Update Draft Tests")
    class SaveOrUpdateDraftTests {

        @DisplayName("Should create new draft when draft does not exist")
        @Test
        void saveOrUpdateDraft_NewDraft_ShouldCreateNew() throws Exception {
            when(objectMapper.writeValueAsString(jsonNode)).thenReturn("{\"field1\":\"value1\"}");
            when(repository.findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L))
                    .thenReturn(Optional.empty());
            when(repository.saveAndFlush(any(Draft.class))).thenReturn(existingDraft);

            Draft result = service.saveOrUpdateDraft(validRequest);

            assertNotNull(result);
            assertEquals("DOC001", result.getDocId());
            assertEquals(1001L, result.getCompanyPoid());
            assertEquals(2001L, result.getUserPoid());

            verify(repository, times(1)).findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L);
            verify(repository, times(1)).saveAndFlush(any(Draft.class));
            verify(objectMapper, times(1)).writeValueAsString(jsonNode);
        }

        @DisplayName("Should update existing draft when draft exists")
        @Test
        void saveOrUpdateDraft_ExistingDraft_ShouldUpdate() throws Exception {
            when(objectMapper.writeValueAsString(jsonNode)).thenReturn("{\"field1\":\"updated\"}");
            when(repository.findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L))
                    .thenReturn(Optional.of(existingDraft));
            when(repository.save(existingDraft)).thenReturn(existingDraft);

            Draft result = service.saveOrUpdateDraft(validRequest);

            assertNotNull(result);
            assertEquals("{\"field1\":\"updated\"}", existingDraft.getDraftData());

            verify(repository, times(1)).findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L);
            verify(repository, times(1)).save(existingDraft);
            verify(repository, never()).saveAndFlush(any());
            verify(objectMapper, times(1)).writeValueAsString(jsonNode);
        }

        @DisplayName("Should handle race condition on insert")
        @Test
        void saveOrUpdateDraft_RaceCondition_ShouldRetryUpdate() throws Exception {
            when(objectMapper.writeValueAsString(jsonNode)).thenReturn("{\"field1\":\"value1\"}");
            when(repository.findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(existingDraft));
            when(repository.saveAndFlush(any(Draft.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate key"));
            when(repository.save(existingDraft)).thenReturn(existingDraft);

            Draft result = service.saveOrUpdateDraft(validRequest);

            assertNotNull(result);
            assertEquals("{\"field1\":\"value1\"}", existingDraft.getDraftData());

            verify(repository, times(2)).findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L);
            verify(repository, times(1)).saveAndFlush(any(Draft.class));
            verify(repository, times(1)).save(existingDraft);
        }

        @DisplayName("Should throw exception when JSON serialization fails")
        @Test
        void saveOrUpdateDraft_SerializationFails_ShouldThrowException() throws Exception {
            when(objectMapper.writeValueAsString(jsonNode))
                    .thenThrow(new RuntimeException("Serialization error"));

            assertThrows(IllegalArgumentException.class, () ->
                    service.saveOrUpdateDraft(validRequest));

            verify(repository, never()).findByDocIdAndCompanyPoidAndUserPoid(any(), any(), any());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Draft Tests")
    class GetDraftTests {

        @DisplayName("Should return draft when found")
        @Test
        void getDraft_Found_ShouldReturnDraft() {
            when(repository.findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L))
                    .thenReturn(Optional.of(existingDraft));

            Optional<Draft> result = service.getDraft("DOC001", 1001L, 2001L);

            assertTrue(result.isPresent());
            assertEquals("DOC001", result.get().getDocId());
            assertEquals(1001L, result.get().getCompanyPoid());
            assertEquals(2001L, result.get().getUserPoid());

            verify(repository, times(1)).findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L);
        }

        @DisplayName("Should return empty when not found")
        @Test
        void getDraft_NotFound_ShouldReturnEmpty() {
            when(repository.findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L))
                    .thenReturn(Optional.empty());

            Optional<Draft> result = service.getDraft("DOC001", 1001L, 2001L);

            assertFalse(result.isPresent());

            verify(repository, times(1)).findByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L);
        }
    }

    @Nested
    @DisplayName("Delete Draft Tests")
    class DeleteDraftTests {

        @DisplayName("Should return true when draft is deleted")
        @Test
        void deleteDraft_Found_ShouldReturnTrue() {
            when(repository.deleteByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L))
                    .thenReturn(1L);

            boolean result = service.deleteDraft("DOC001", 1001L, 2001L);

            assertTrue(result);

            verify(repository, times(1)).deleteByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L);
        }

        @DisplayName("Should return false when no draft is deleted")
        @Test
        void deleteDraft_NotFound_ShouldReturnFalse() {
            when(repository.deleteByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L))
                    .thenReturn(0L);

            boolean result = service.deleteDraft("DOC001", 1001L, 2001L);

            assertFalse(result);

            verify(repository, times(1)).deleteByDocIdAndCompanyPoidAndUserPoid("DOC001", 1001L, 2001L);
        }
    }

    @Nested
    @DisplayName("JSON Serialization Tests")
    class JsonSerializationTests {

        @DisplayName("Should deserialize JSON successfully")
        @Test
        void deserialize_ValidJson_ShouldReturnJsonNode() throws Exception {
            String json = "{\"field1\":\"value1\"}";
            JsonNode expectedNode = mock(JsonNode.class);

            when(objectMapper.readTree(json)).thenReturn(expectedNode);

            JsonNode result = service.deserialize(json);

            assertNotNull(result);
            assertEquals(expectedNode, result);

            verify(objectMapper, times(1)).readTree(json);
        }

        @DisplayName("Should throw exception when deserialization fails")
        @Test
        void deserialize_InvalidJson_ShouldThrowException() throws Exception {
            String invalidJson = "invalid json";

            when(objectMapper.readTree(invalidJson))
                    .thenThrow(new RuntimeException("Parse error"));

            assertThrows(IllegalStateException.class, () ->
                    service.deserialize(invalidJson));

            verify(objectMapper, times(1)).readTree(invalidJson);
        }
    }
}