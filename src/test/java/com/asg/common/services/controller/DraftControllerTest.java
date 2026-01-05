package com.asg.common.services.controller;

import com.asg.common.services.dto.SaveDraftRequest;
import com.asg.common.services.entity.Draft;
import com.asg.common.services.exceptions.GlobalExceptionHandler;
import com.asg.common.services.service.DraftService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DraftControllerTest {

    @Mock
    private DraftService service;

    @InjectMocks
    private DraftController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSaveDraft_success() throws Exception {
        String requestJson = """
        {
            "docId": "DOC001",
            "companyPoid": 1001,
            "userPoid": 2001,
            "jsonData": {
                "field1": "value1",
                "field2": "value2"
            }
        }
        """;

        Draft savedDraft = new Draft();
        savedDraft.setDocId("DOC001");
        savedDraft.setCompanyPoid(1001L);
        savedDraft.setUserPoid(2001L);

        when(service.saveOrUpdateDraft(any(SaveDraftRequest.class))).thenReturn(savedDraft);

        mockMvc.perform(post("/api/v1/draft/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Draft saved successfully"))
                .andExpect(jsonPath("$.result.data.docId").value("DOC001"))
                .andExpect(jsonPath("$.result.data.companyPoid").value(1001))
                .andExpect(jsonPath("$.result.data.userId").value(2001));
    }

    @Test
    void testSaveDraft_serviceThrows_returns500() throws Exception {
        String requestJson = """
        {
            "docId": "DOC001",
            "companyPoid": 1001,
            "userPoid": 2001,
            "jsonData": {
                "field1": "value1"
            }
        }
        """;

        when(service.saveOrUpdateDraft(any(SaveDraftRequest.class)))
                .thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(post("/api/v1/draft/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetDraft_success() throws Exception {
        Draft draft = new Draft();
        draft.setDocId("DOC001");
        draft.setCompanyPoid(1001L);
        draft.setUserPoid(2001L);
        draft.setDraftData("{\"field1\":\"value1\"}");
        draft.setLastModifiedDate(OffsetDateTime.now());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonData = mapper.createObjectNode();
        jsonData.put("field1", "value1");

        when(service.getDraft("DOC001", 1001L, 2001L)).thenReturn(Optional.of(draft));
        when(service.deserialize("{\"field1\":\"value1\"}")).thenReturn(jsonData);

        mockMvc.perform(get("/api/v1/draft/1001/2001/DOC001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.docId").value("DOC001"))
                .andExpect(jsonPath("$.result.data.companyPoid").value(1001))
                .andExpect(jsonPath("$.result.data.userPoid").value(2001))
                .andExpect(jsonPath("$.message").value("Draft fetched successfully"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetDraft_notFound() throws Exception {
        when(service.getDraft("DOC001", 1001L, 2001L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/draft/1001/2001/DOC001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No draft found for docId=DOC001, companyId=1001, userId=2001"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").isEmpty());
    }

    @Test
    void testGetDraft_serviceThrows_returns500() throws Exception {
        when(service.getDraft("DOC001", 1001L, 2001L))
                .thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(get("/api/v1/draft/1001/2001/DOC001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testDeleteDraft_success() throws Exception {
        when(service.deleteDraft("DOC001", 1001L, 2001L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/draft/1001/2001/DOC001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Draft deleted successfully for docId=DOC001, companyId=1001, userId=2001"));
    }

    @Test
    void testDeleteDraft_notFound() throws Exception {
        when(service.deleteDraft("DOC001", 1001L, 2001L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/draft/1001/2001/DOC001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("No draft found to delete for docId=DOC001, companyId=1001, userId=2001"));
    }

    @Test
    void testDeleteDraft_serviceThrows_returns500() throws Exception {
        when(service.deleteDraft("DOC001", 1001L, 2001L))
                .thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(delete("/api/v1/draft/1001/2001/DOC001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetDraft_blankDocId_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/draft/1001/2001/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testDeleteDraft_blankDocId_returns400() throws Exception {
        mockMvc.perform(delete("/api/v1/draft/1001/2001/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}