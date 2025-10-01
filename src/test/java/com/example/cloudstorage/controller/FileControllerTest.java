package com.example.cloudstorage.controller;

import com.example.cloudstorage.dto.RenameFileRequest;
import com.example.cloudstorage.dto.SuccessResponse;
import com.example.cloudstorage.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    void renameFile_successfulRename_returnsOk() throws IOException {
        String filename = "oldName.txt";
        String newName = "newName.txt";

        // создаём DTO вместо Map
        RenameFileRequest body = new RenameFileRequest();
        body.setName(newName);

        // вызываем метод напрямую, он теперь возвращает SuccessResponse
        SuccessResponse response = fileController.renameFile(filename, body, authentication);

        assertEquals("File renamed successfully", response.getMessage());

        // проверяем, что сервис вызван с правильными параметрами
        verify(fileService).renameFileFromController(filename, body, "testUser");
    }
}
