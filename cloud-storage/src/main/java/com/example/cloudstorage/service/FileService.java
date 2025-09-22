package com.example.cloudstorage.service;

import com.example.cloudstorage.entity.File;
import com.example.cloudstorage.entity.User;
import com.example.cloudstorage.repository.FileRepository;
import com.example.cloudstorage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final String uploadPath = "/uploads";

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getFiles(String username, int limit) {
        logger.info("Fetching files for user: {}, limit: {}", username, limit);
        return fileRepository.findByUserUsername(username)
                .stream()
                .map(file -> {
                    try {
                        Path path = Paths.get(file.getFilepath());
                        logger.debug("Checking file path: {}", path);
                        int size = Files.exists(path) ? (int) Files.size(path) : 0;
                        return Map.<String, Object>of("filename", file.getFilename(), "size", size);
                    } catch (IOException e) {
                        logger.warn("Failed to get size for file: {}. Error: {}", file.getFilepath(), e.getMessage());
                        return Map.<String, Object>of("filename", file.getFilename(), "size", 0);
                    }
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void uploadFile(MultipartFile file, String username, String filename) {
        logger.info("Uploading file: {} for user: {}", filename, username);
        try {
            Path path = Paths.get(uploadPath, username, filename);
            logger.debug("Saving file to: {}", path);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            fileRepository.findTopByFilenameAndUserUsername(filename, username)
                    .ifPresent(existingFile -> {
                        logger.info("Deleting existing file: {}", filename);
                        fileRepository.delete(existingFile);
                    });
            File fileEntity = new File();
            fileEntity.setFilename(filename);
            fileEntity.setFilepath(path.toString());
            fileEntity.setUploadedAt(LocalDateTime.now());
            fileEntity.setUser(user);
            fileRepository.save(fileEntity);
            logger.info("File uploaded successfully: {}", filename);
        } catch (IOException e) {
            logger.error("Failed to upload file: {}. Error: {}", filename, e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    public PathResource getFileResource(String filename, String username) {
        logger.info("Downloading file: {} for user: {}", filename, username);
        File file = fileRepository.findTopByFilenameAndUserUsername(filename, username)
                .orElseThrow(() -> {
                    logger.error("File not found: {}", filename);
                    return new RuntimeException("File not found: " + filename);
                });
        Path path = Paths.get(file.getFilepath());
        logger.debug("Checking file path: {}", path);
        if (!Files.exists(path)) {
            logger.error("File not found on disk: {}", file.getFilepath());
            throw new RuntimeException("File not found on disk: " + file.getFilepath());
        }
        return new PathResource(path);
    }

    public void renameFile(String filename, String newName, String username) {
        logger.info("Attempting to rename file: {} to {} for user: {}", filename, newName, username);
        File file = fileRepository.findTopByFilenameAndUserUsername(filename, username)
                .orElseThrow(() -> {
                    logger.error("File not found in database: {}", filename);
                    return new RuntimeException("File not found: " + filename);
                });
        logger.debug("Found file in database: {} with path: {}", file.getFilename(), file.getFilepath());
        try {
            Path oldPath = Paths.get(file.getFilepath());
            Path newPath = oldPath.getParent().resolve(newName);
            logger.debug("Renaming file on disk from {} to {}", oldPath, newPath);
            if (!Files.exists(oldPath)) {
                logger.error("File does not exist on disk: {}", oldPath);
                throw new RuntimeException("File does not exist on disk: " + oldPath);
            }
            Files.move(oldPath, newPath);
            file.setFilename(newName);
            file.setFilepath(newPath.toString());
            fileRepository.save(file);
            logger.info("File renamed successfully: {} to {}", filename, newName);
        } catch (IOException e) {
            logger.error("Failed to rename file: {}. Error: {}", filename, e.getMessage());
            throw new RuntimeException("Failed to rename file: " + e.getMessage());
        }
    }

    public void deleteFile(String filename, String username) {
        logger.info("Deleting file: {} for user: {}", filename, username);
        File file = fileRepository.findTopByFilenameAndUserUsername(filename, username)
                .orElseThrow(() -> {
                    logger.error("File not found: {}", filename);
                    return new RuntimeException("File not found: " + filename);
                });
        try {
            Path path = Paths.get(file.getFilepath());
            logger.debug("Deleting file: {}", path);
            Files.deleteIfExists(path);
            fileRepository.delete(file);
            logger.info("File deleted successfully: {}", filename);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}. Error: {}", filename, e.getMessage());
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }
}