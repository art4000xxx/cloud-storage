package com.example.cloudstorage.controller;

import com.example.cloudstorage.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private FileService fileService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("testUser");
    }

    @Test
    void renameFile_successfulRename_returnsOk() {
        String filename = "oldName.txt";
        String newName = "newName.txt";
        Map<String, String> body = new HashMap<>();
        body.put("name", newName);

        ResponseEntity<String> response = fileController.renameFile(filename, body, authentication);

        assertEquals(OK, response.getStatusCode());
        assertEquals("File renamed successfully", response.getBody());
        verify(fileService).renameFile(filename, newName, "testUser");
    }
}