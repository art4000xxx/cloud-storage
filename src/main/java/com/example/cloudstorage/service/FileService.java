package com.example.cloudstorage.service;

import com.example.cloudstorage.dto.FileDto;
import com.example.cloudstorage.dto.RenameFileRequest;
import com.example.cloudstorage.entity.File;
import com.example.cloudstorage.entity.User;
import com.example.cloudstorage.repository.FileRepository;
import com.example.cloudstorage.repository.UserRepository;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final String uploadPath = "/uploads";

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public List<FileDto> getFiles(String username, int limit) {
        return fileRepository.findByUserUsername(username)
                .stream()
                .limit(limit)
                .map(f -> {
                    Path path = Paths.get(f.getFilepath());
                    long size = Files.exists(path) ? path.toFile().length() : 0;
                    return new FileDto(f.getFilename(), size, f.getUploadedAt());
                })
                .collect(Collectors.toList());
    }

    public void uploadFile(MultipartFile file, String username, String filename) throws IOException {
        Path path = Paths.get(uploadPath, username, filename);
        Files.createDirectories(path.getParent());
        file.transferTo(path);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        fileRepository.findTopByFilenameAndUserUsername(filename, username)
                .ifPresent(fileRepository::delete);

        File fileEntity = new File();
        fileEntity.setFilename(filename);
        fileEntity.setFilepath(path.toString());
        fileEntity.setUploadedAt(LocalDateTime.now());
        fileEntity.setUser(user);

        fileRepository.save(fileEntity);
    }

    public PathResource getFileResource(String filename, String username) {
        File file = fileRepository.findTopByFilenameAndUserUsername(filename, username)
                .orElseThrow(() -> new RuntimeException("File not found: " + filename));

        Path path = Paths.get(file.getFilepath());
        if (!Files.exists(path)) {
            throw new RuntimeException("File not found on disk: " + file.getFilepath());
        }
        return new PathResource(path);
    }

    public void renameFile(String filename, String newName, String username) throws IOException {
        File file = fileRepository.findTopByFilenameAndUserUsername(filename, username)
                .orElseThrow(() -> new RuntimeException("File not found: " + filename));

        Path oldPath = Paths.get(file.getFilepath());
        Path newPath = oldPath.getParent().resolve(newName);

        if (!Files.exists(oldPath)) {
            throw new RuntimeException("File does not exist on disk: " + oldPath);
        }

        Files.move(oldPath, newPath);
        file.setFilename(newName);
        file.setFilepath(newPath.toString());
        fileRepository.save(file);
    }

    public void renameFileFromController(String filename, RenameFileRequest request, String username) throws IOException {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new RuntimeException("Missing or empty new name");
        }
        renameFile(filename, request.getName(), username);
    }

    public void deleteFile(String filename, String username) throws IOException {
        File file = fileRepository.findTopByFilenameAndUserUsername(filename, username)
                .orElseThrow(() -> new RuntimeException("File not found: " + filename));

        Path path = Paths.get(file.getFilepath());
        Files.deleteIfExists(path);
        fileRepository.delete(file);
    }
}
