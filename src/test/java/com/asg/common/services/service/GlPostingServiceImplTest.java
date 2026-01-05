package com.asg.common.services.service;

import com.asg.common.services.repository.GlPostingRepository;
import com.asg.common.services.service.impl.GlPostingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GlPostingServiceImplTest {

    @Mock
    private GlPostingRepository repository;


    @InjectMocks
    private GlPostingServiceImpl glPostingService;


    @BeforeEach
    void setUp() {
    }


    @Test
    void fetchGlPostings_ShouldPropagateException_WhenServiceThrows() throws SQLException {
        when(repository.getGlPostings("DOC123", 1001L))
                .thenThrow(new RuntimeException("Database connection failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> glPostingService.fetchGlPostings("DOC123", 1001L));

        assertEquals("Database connection failed", exception.getMessage());
    }

    @Test
    void glreposting_ShouldReturnSuccessMessage_WhenRepositorySucceeds() {

        int groupPoid = 1;
        int companyPoid = 2;
        int userPoid = 3;
        String docId = "DOC123";
        int transactionPoid = 4;
        int docRef = 5;
        String expectedMessage = "GL reposting completed successfully";

        when(repository.glreposting(
                eq(groupPoid),
                eq(companyPoid),
                eq(userPoid),
                eq(docId),
                eq(transactionPoid),
                eq(docRef)))
                .thenReturn(expectedMessage);

        String result = glPostingService.glreposting(
                groupPoid, companyPoid, userPoid, docId, transactionPoid, docRef);

        assertEquals(expectedMessage, result);
        verify(repository, times(1)).glreposting(
                groupPoid, companyPoid, userPoid, docId, transactionPoid, docRef);
    }

    @Test
    void glreposting_ShouldHandleRepositoryException() {
        int groupPoid = 1;
        int companyPoid = 2;
        int userPoid = 3;
        String docId = "DOC123";
        int transactionPoid = 4;
        int docRef = 5;

        when(repository.glreposting(
                anyInt(), anyInt(), anyInt(), anyString(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                glPostingService.glreposting(
                        groupPoid, companyPoid, userPoid, docId, transactionPoid, docRef)
        );

        assertEquals("Database error", exception.getMessage());
        verify(repository, times(1)).glreposting(
                groupPoid, companyPoid, userPoid, docId, transactionPoid, docRef);
    }

    @Test
    void glreposting_ShouldHandleEmptyDocId() {
        int groupPoid = 1;
        int companyPoid = 2;
        int userPoid = 3;
        String emptyDocId = "";
        int transactionPoid = 4;
        int docRef = 5;
        String expectedMessage = "GL reposting completed with empty document ID";

        when(repository.glreposting(
                eq(groupPoid),
                eq(companyPoid),
                eq(userPoid),
                eq(emptyDocId),
                eq(transactionPoid),
                eq(docRef)))
                .thenReturn(expectedMessage);

        String result = glPostingService.glreposting(
                groupPoid, companyPoid, userPoid, emptyDocId, transactionPoid, docRef);

        assertEquals(expectedMessage, result);
        verify(repository, times(1)).glreposting(
                groupPoid, companyPoid, userPoid, emptyDocId, transactionPoid, docRef);
    }
}
