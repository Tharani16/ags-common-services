package com.asg.common.services.controller;

import com.asg.common.lib.service.LovDataService;
import com.asg.common.lib.utility.PaginationProperties;
import com.asg.common.services.service.StateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LovControllerTest {

    @Mock
    private LovDataService lovService;

    @Mock
    private PaginationProperties paginationProperties;

    @Mock
    private StateService stateService;

    @InjectMocks
    private LovController lovController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lovController).build();

        lenient().when(paginationProperties.getPageNumber()).thenReturn(0);
        lenient().when(paginationProperties.getPageSize()).thenReturn(10);
        lenient().when(paginationProperties.getSortBy()).thenReturn("code");
        lenient().when(paginationProperties.getSortDir()).thenReturn("asc");

        // Mock LovService for successful responses
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("data", Collections.emptyList());
        mockResult.put("totalRecords", 0);
        mockResult.put("defaultValues", Collections.emptyList());
        lenient().when(lovService.getLovList(any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any()))
                .thenReturn(mockResult);
        lenient().when(lovService.getLovList(any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any(), anyList(), anyList()))
                .thenReturn(mockResult);
    }

    @Test
    void getTaskPriority_ReturnsStaticList() throws Exception {
        mockMvc.perform(get("/v1/lovs/task-priority"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task Priority fetched successfully"))
                .andExpect(jsonPath("$.result.data.data").isArray())
                .andExpect(jsonPath("$.result.data.data[0].code").value("LOW"));
    }

    @Test
    void getTaskStatus_ReturnsStaticList() throws Exception {
        mockMvc.perform(get("/v1/lovs/task-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task Status fetched successfully"))
                .andExpect(jsonPath("$.result.data.data").isArray())
                .andExpect(jsonPath("$.result.data.data[0].code").value("PENDING"));
    }

    @Test
    void getDataEntryPeriods_ReturnsStaticList() throws Exception {
        mockMvc.perform(get("/v1/lovs/data-entry-periods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Data Entry Periods fetched successfully"))
                .andExpect(jsonPath("$.result.data.data").isArray())
                .andExpect(jsonPath("$.result.data.data[0].code").value("FINANCIAL"));
    }

    @Test
    void getApprovalLevels_ReturnsStaticList() throws Exception {
        mockMvc.perform(get("/v1/lovs/approval-levels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Approval Levels fetched successfully"))
                .andExpect(jsonPath("$.result.data.data").isArray())
                .andExpect(jsonPath("$.result.data.data[0].code").value("LEVEL1"));
    }

    @Test
    void getStatesByCountry_ReturnsSuccess() throws Exception {
        when(stateService.getStatesByCountry(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/lovs/countries/1/states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("States fetched successfully"));
    }

    @Test
    void getWithInvalidSortBy_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortBy", "invalid_field"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid sortBy field. Allowed values: [poid, code, label, description, value]"));
    }

    @Test
    void getWithInvalidSortDir_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortDir", "invalid_direction"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid sortDir value. Allowed values: asc, desc"));
    }

    // Edge Cases
    @Test
    void getStatesByCountry_WithNullCountryId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/countries/null/states"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatesByCountry_WithNegativeCountryId_ReturnsSuccess() throws Exception {
        when(stateService.getStatesByCountry(-1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/lovs/countries/-1/states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getStatesByCountry_WithZeroCountryId_ReturnsSuccess() throws Exception {
        when(stateService.getStatesByCountry(0L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/lovs/countries/0/states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithNegativePageNumber_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("pageNumber", "-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithZeroPageSize_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("pageSize", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithNegativePageSize_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("pageSize", "-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithExtremelyLargePageSize_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("pageSize", "999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithEmptyStringParameters_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortBy", "")
                        .param("sortDir", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithWhitespaceParameters_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortBy", "   ")
                        .param("sortDir", "\t\n"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithSpecialCharactersInParameters_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortBy", "code'; DROP TABLE--"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getWithCaseSensitiveSortBy_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortBy", "CODE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithCaseSensitiveSortDir_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortDir", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getWithNonNumericPageNumber_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("pageNumber", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithNonNumericPageSize_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("pageSize", "xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithMultipleSortByParameters_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortBy", "code")
                        .param("sortBy", "label"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithUnicodeCharacters_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortBy", "cödé"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithVeryLongParameterValue_ReturnsBadRequest() throws Exception {
        String longValue = "a".repeat(1000);
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("sortBy", longValue))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatesByCountry_WithVeryLargeCountryId_ReturnsSuccess() throws Exception {
        when(stateService.getStatesByCountry(Long.MAX_VALUE)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/lovs/countries/" + Long.MAX_VALUE + "/states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getStatesByCountry_WithNonNumericCountryId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/countries/abc/states"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithDecimalPageNumber_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("pageNumber", "1.5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithDecimalPageSize_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/v1/lovs/currencies")
                        .param("pageSize", "10.5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProperties() throws Exception {
        lenient(). when(lovService.getLovList(anyString(), anyLong(), anyLong(), anyLong(),
                        eq("PROPERTIES"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Map.of("data", "properties"));

        mockMvc.perform(get("/v1/lovs/properties").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetInsuranceContactTypes() throws Exception {
        lenient().when(lovService.getLovList(anyString(), anyLong(), anyLong(), anyLong(),
                        eq("INSURANCE_CONTACT_TYPE"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Map.of("data", "insuranceContactTypes"));

        mockMvc.perform(get("/v1/lovs/insurance-contact-types").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetInsuranceCategories() throws Exception {
        lenient().when(lovService.getLovList(anyString(), anyLong(), anyLong(), anyLong(),
                        eq("INSURANCE_CATEGORY"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Map.of("data", "insuranceCategories"));

        mockMvc.perform(get("/v1/lovs/insurance-categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetInsuranceTypes() throws Exception {
        lenient().when(lovService.getLovList(anyString(), anyLong(), anyLong(), anyLong(),
                        eq("INSURANCE_TYPE"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Map.of("data", "insuranceTypes"));

        mockMvc.perform(get("/v1/lovs/insurance-types").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPicPersonList() throws Exception {
        lenient(). when(lovService.getLovList(anyString(), anyLong(), anyLong(), anyLong(),
                        eq("INSURANCE_PROPERTY_PIC"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Map.of("data", "picPersons"));

        mockMvc.perform(get("/v1/lovs/pic-person-list").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLovList() throws Exception {
        lenient(). when(lovService.getLovList(
                anyString(),
                anyLong(),
                anyLong(),
                anyLong(),
                eq("FIXED_ASSET"),
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                any(),
                any()
        )).thenReturn(Map.of("data", "lovList"));

        mockMvc.perform(get("/v1/lovs/FIXED_ASSET")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled
    void testGetLovListWithDefaultCode() throws Exception {
        Map<String, Object> mockResultWithDefault = new HashMap<>();
        mockResultWithDefault.put("data", Collections.emptyList());
        mockResultWithDefault.put("totalRecords", 0);
        mockResultWithDefault.put("defaultValues", List.of(Map.of("poid", 1L, "code", "DEFAULT_CODE")));

        when(lovService.getLovList(
                isNull(),
                eq(1L),
                eq(0L),
                eq(0L),
                eq("CURRENCY"),
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                eq(List.of("USD")),
                isNull(),
                isNull()
        )).thenReturn(mockResultWithDefault);

        mockMvc.perform(get("/v1/lovs/CURRENCY")
                        .param("defaultCode", "USD")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.defaultValues[0].code").value("DEFAULT_CODE"));
    }

    @Test
    @Disabled
    void testGetLovListWithDefaultPoid() throws Exception {
        Map<String, Object> mockResultWithDefault = new HashMap<>();
        mockResultWithDefault.put("data", Collections.emptyList());
        mockResultWithDefault.put("totalRecords", 0);
        mockResultWithDefault.put("defaultValues", List.of(Map.of("poid", 123L, "code", "TEST_CODE")));

        when(lovService.getLovList(
                isNull(),
                eq(1L),
                eq(0L),
                eq(0L),
                eq("CURRENCY"),
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                isNull(),
                eq(List.of(123L)),
                isNull()
        )).thenReturn(mockResultWithDefault);

        mockMvc.perform(get("/v1/lovs/CURRENCY")
                        .param("defaultPoid", "123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.defaultValues[0].poid").value(123));
    }

    @Test
    @Disabled
    void testGetLovListWithBothDefaults() throws Exception {
        Map<String, Object> mockResultWithDefault = new HashMap<>();
        mockResultWithDefault.put("data", Collections.emptyList());
        mockResultWithDefault.put("totalRecords", 0);
        mockResultWithDefault.put("defaultValues", List.of(Map.of("poid", 456L, "code", "BOTH_CODE")));

        when(lovService.getLovList(
                isNull(),
                eq(1L),
                eq(0L),
                eq(0L),
                eq("CURRENCY"),
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                eq(List.of("EUR")),
                eq(List.of(456L)),
                isNull()
        )).thenReturn(mockResultWithDefault);

        mockMvc.perform(get("/v1/lovs/CURRENCY")
                        .param("defaultCode", "EUR")
                        .param("defaultPoid", "456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.defaultValues[0].code").value("BOTH_CODE"))
                .andExpect(jsonPath("$.result.defaultValues[0].poid").value(456));
    }

    @Test
    void testGetLovListWithInvalidDefaultPoid() throws Exception {
        mockMvc.perform(get("/v1/lovs/CURRENCY")
                        .param("defaultPoid", "invalid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void testGetLovListWithNegativeDefaultPoid() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("data", Collections.emptyList());
        mockResult.put("totalRecords", 0);
        mockResult.put("defaultValues", Collections.emptyList());

        when(lovService.getLovList(
                isNull(),
                eq(1L),
                eq(0L),
                eq(0L),
                eq("CURRENCY"),
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                isNull(),
                eq(List.of(-1L)),
                isNull()
        )).thenReturn(mockResult);

        mockMvc.perform(get("/v1/lovs/CURRENCY")
                        .param("defaultPoid", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.defaultValues").isEmpty());
    }

    @Test
    @Disabled
    void testGetLovListWithEmptyDefaultCode() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("data", Collections.emptyList());
        mockResult.put("totalRecords", 0);
        mockResult.put("defaultValues", Collections.emptyList());

        when(lovService.getLovList(
                isNull(),
                eq(1L),
                eq(0L),
                eq(0L),
                eq("CURRENCY"),
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                eq(List.of("")),
                isNull(),
                isNull()
        )).thenReturn(mockResult);

        mockMvc.perform(get("/v1/lovs/CURRENCY")
                        .param("defaultCode", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLovListWithLargeDefaultPoid() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("data", Collections.emptyList());
        mockResult.put("totalRecords", 0);
        mockResult.put("defaultValues", Collections.emptyList());

        when(lovService.getLovList(
                isNull(),
                eq(1L),
                eq(0L),
                eq(0L),
                eq("CURRENCY"),
                anyInt(),
                anyInt(),
                anyString(),
                anyString(),
                isNull(),
                eq(List.of(Long.MAX_VALUE)),
                isNull()
        )).thenReturn(mockResult);

        mockMvc.perform(get("/v1/lovs/CURRENCY")
                        .param("defaultPoid", String.valueOf(Long.MAX_VALUE))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}


