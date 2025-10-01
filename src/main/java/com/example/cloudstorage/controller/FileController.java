package com.example.cloudstorage.controller;

import com.example.cloudstorage.dto.*;
import com.example.cloudstorage.service.FileService;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cloud")
@CrossOrigin(origins = "http://localhost:8081")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/list")
    public List<FileDto> getFiles(Authentication authentication,
                                  @RequestParam(value = "limit", defaultValue = "10") int limit) throws IOException {
        return fileService.getFiles(authentication.getName(), limit);
    }

    @PostMapping("/file")
    public SuccessResponse uploadFile(@RequestParam("filename") String filename,
                                      @RequestParam("file") MultipartFile file,
                                      Authentication authentication) throws IOException {
        fileService.uploadFile(file, authentication.getName(), filename);
        return new SuccessResponse("File uploaded successfully");
    }

    @GetMapping("/file")
    public ResponseEntity<PathResource> downloadFile(@RequestParam("filename") String filename,
                                                     Authentication authentication) {
        PathResource resource = fileService.getFileResource(filename, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PutMapping("/file")
    public SuccessResponse renameFile(@RequestParam("filename") String filename,
                                      @RequestBody RenameFileRequest request,
                                      Authentication authentication) throws IOException {
        fileService.renameFileFromController(filename, request, authentication.getName());
        return new SuccessResponse("File renamed successfully");
    }

    @DeleteMapping("/file")
    public SuccessResponse deleteFile(@RequestParam("filename") String filename,
                                      Authentication authentication) throws IOException {
        fileService.deleteFile(filename, authentication.getName());
        return new SuccessResponse("File deleted successfully");
    }
}
