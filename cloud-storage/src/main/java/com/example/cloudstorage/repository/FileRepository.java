package com.example.cloudstorage.repository;

import com.example.cloudstorage.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUserUsername(String username);
    Optional<File> findTopByFilenameAndUserUsername(String filename, String username);
}