package com.example.cloudstorage.controller;

import com.example.cloudstorage.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getFilesWithLimit(Authentication authentication, @RequestParam(value = "limit", defaultValue = "10") int limit) {
        String username = authentication.getName();
        logger.info("Fetching file list for user: {}, limit: {}", username, limit);
        return ResponseEntity.ok(fileService.getFiles(username, limit));
    }

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestParam("filename") String filename, @RequestParam("file") MultipartFile file, Authentication authentication) {
        String username = authentication.getName();
        logger.info("Uploading file: {} for user: {}", filename, username);
        fileService.uploadFile(file, username, filename);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename, Authentication authentication) {
        String username = authentication.getName();
        logger.info("Downloading file: {} for user: {}", filename, username);
        PathResource fileResource = fileService.getFileResource(filename, username);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileResource);
    }

    @PutMapping("/file")
    public ResponseEntity<String> renameFile(@RequestParam("filename") String filename, @RequestBody Map<String, String> body, Authentication authentication) {
        logger.info("Received PUT /file request for filename: {}", filename);
        logger.debug("Request body: {}", body);
        if (body == null || body.isEmpty()) {
            logger.warn("Invalid rename request: empty body for file: {}", filename);
            return ResponseEntity.badRequest().body("{\"message\":\"Request body is empty\",\"id\":400}");
        }
        String newName = body.get("name");
        if (newName == null) {
            newName = body.get("newName");
        }
        if (newName == null) {
            newName = body.values().stream()
                    .filter(val -> val != null && !val.trim().isEmpty())
                    .findFirst()
                    .orElse(null);
        }
        if (newName == null || newName.trim().isEmpty()) {
            logger.warn("Invalid rename request: missing or empty name in body for file: {}", filename);
            return ResponseEntity.badRequest().body("{\"message\":\"Missing or empty name\",\"id\":400}");
        }
        String username = authentication.getName();
        logger.info("Renaming file: {} to {} for user: {}", filename, newName, username);
        try {
            fileService.renameFile(filename, newName, username);
            logger.info("File renamed successfully: {} to {}", filename, newName);
            return ResponseEntity.ok("File renamed successfully");
        } catch (Exception e) {
            logger.error("Failed to rename file: {}. Error: {}", filename, e.getMessage());
            return ResponseEntity.status(500).body("{\"message\":\"Failed to rename file: " + e.getMessage() + "\",\"id\":500}");
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") String filename, Authentication authentication) {
        String username = authentication.getName();
        logger.info("Deleting file: {} for user: {}", filename, username);
        fileService.deleteFile(filename, username);
        return ResponseEntity.ok("File deleted successfully");
    }

    @PostMapping("/file-login")
    public ResponseEntity<Void> login() {
        logger.info("Processing login via FileController");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        logger.info("Processing logout via FileController");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    public ResponseEntity<Void> handleLogout(@RequestParam(value = "logout", required = false) String logout) {
        logger.info("Processing GET /login?logout request");
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/file", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        logger.info("Received OPTIONS request for /file");
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "http://localhost:8081")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }
}