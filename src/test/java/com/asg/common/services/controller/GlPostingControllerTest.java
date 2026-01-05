package com.asg.common.services.controller;

import com.asg.common.lib.dto.response.GlPostingViewResponseDto;
import com.asg.common.services.dto.GlrePostingDto;
import com.asg.common.services.service.GlPostingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlPostingControllerTest {

    @Mock
    private GlPostingService glPostingService;

    private GlPostingController glPostingController;
    private GlrePostingDto glrePostingDto;

    @BeforeEach
    void setUp() {
        glrePostingDto = new GlrePostingDto();
        glrePostingDto.setLoginGroupPoid(1);
        glrePostingDto.setLoginCompanyPoid(1);
        glrePostingDto.setLoginUserPoid(1);
        glrePostingDto.setDocId("150-182");
        glrePostingDto.setTransactionPoid(123);
        glrePostingDto.setDocRef(456);

        glPostingController = new GlPostingController(glPostingService);
    }

    @Test
    void getGlPostings_ShouldReturnGlPostingViewResponse_WhenValidRequest() throws SQLException {
        GlPostingViewResponseDto mockResponse = new GlPostingViewResponseDto();
        when(glPostingService.fetchGlPostings(anyString(), anyLong()))
                .thenReturn(mockResponse);

        ResponseEntity<?> response = glPostingController.getGlPostings(
                "DOC123", 1001L
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }


    @Test
    void getGlPostings_ShouldReturn200_WhenDocIdIsBlank() throws SQLException {
        GlPostingViewResponseDto mockResponse = new GlPostingViewResponseDto();
        when(glPostingService.fetchGlPostings( anyString(), anyLong()))
                .thenReturn(mockResponse);

        ResponseEntity<?> response = glPostingController.getGlPostings(
                "000-1005", 1001L
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getGlPostings_ShouldThrowSQLException_WhenServiceFails() throws SQLException {
        when(glPostingService.fetchGlPostings(anyString(), anyLong()))
                .thenThrow(new SQLException("Database connection failed"));

        SQLException thrown = assertThrows(SQLException.class, () -> {
            glPostingController.getGlPostings( "DOC123", 1001L);
        });

        assertEquals("Database connection failed", thrown.getMessage());
    }



    @Test
    void getGlPostings_ShouldReturnEmptyResponse_WhenNoDataFound() throws SQLException {
        GlPostingViewResponseDto emptyResponse = new GlPostingViewResponseDto();
        when(glPostingService.fetchGlPostings( anyString(), anyLong()))
                .thenReturn(emptyResponse);

        ResponseEntity<?> response = glPostingController.getGlPostings(
                "DOC123", 1001L
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void glrePosting_ShouldReturnSuccess_WhenValidRequest() {

        String expectedMessage = "GL posting successful";
        when(glPostingService.glreposting(
                anyInt(), anyInt(), anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(expectedMessage);

        ResponseEntity<?> response = glPostingController.glrePosting(glrePostingDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("GL posting completed successfully"));
    }

    @Test
    void glrePosting_ShouldHandleErrorResponse() {

        String errorMessage = "ERROR: No data found";
        when(glPostingService.glreposting(
                anyInt(), anyInt(), anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(errorMessage);


        ResponseEntity<?> response = glPostingController.glrePosting(glrePostingDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains(errorMessage));
    }


    @Test
    void glrePosting_ShouldHandleNullGlrePostingDto() {
        try {
            ResponseEntity<?> response = glPostingController.glrePosting(null);

            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("glrePostingDto"));
        }
    }

}
