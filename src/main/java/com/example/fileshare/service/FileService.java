package com.example.fileshare.service;

import com.example.fileshare.FileEntity;
import com.example.fileshare.FileRepository;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository repository;

    public Map<String, String> upload(MultipartFile file, String password, Long ttlMinutes) {


        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = Paths.get("uploads/" + fileName);

            Files.createDirectories(path.getParent());

            Files.write(path, file.getBytes());

            FileEntity entity = new FileEntity();
            if (password != null && !password.isBlank()) {
                entity.setPasswordHash(passwordEncoder.encode(password));
            }
            entity.setOriginalName(file.getOriginalFilename());
            entity.setStoragePath(path.toString());
            entity.setSize(file.getSize());
            entity.setUploadTime(LocalDateTime.now());
            if (ttlMinutes != null) {
                entity.setExpireAt(LocalDateTime.now().plusMinutes(ttlMinutes));
            }

            repository.save(entity);

            return Map.of(
                    "fileId", entity.getId().toString(),
                    "downloadUrl", "http://localhost:8080/files/" + entity.getId()
            );

        } catch (IOException e) {
            throw new RuntimeException("Upload failed");
        }



    }

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<Resource> download(UUID id, String password) {
        try {

            FileEntity file = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found"));

            Path path = Paths.get(file.getStoragePath());

            if (file.getExpireAt() != null &&
                    file.getExpireAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("File expired");
            }

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found on disk");
            }

            file.setDownloadCount(file.getDownloadCount() + 1);

            repository.save(file);
            if (file.getPasswordHash() != null) {
                if (password == null || !passwordEncoder.matches(password, file.getPasswordHash())) {
                    throw new RuntimeException("Invalid password");
                }
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Download failed");
        }
    }


}