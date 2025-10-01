package com.example.cloudstorage.dto;

import java.time.LocalDateTime;

public class FileDto {
    private String filename;
    private long size;
    private LocalDateTime uploadedAt;

    public FileDto() {}
    public FileDto(String filename, long size, LocalDateTime uploadedAt) {
        this.filename = filename;
        this.size = size;
        this.uploadedAt = uploadedAt;
    }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
