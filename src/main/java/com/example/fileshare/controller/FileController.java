package com.example.fileshare.controller;

import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.fileshare.service.FileService;

import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "ttlMinutes", required = false) Long ttlMinutes
    ) {
        return ResponseEntity.ok(fileService.upload(file, password, ttlMinutes));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable UUID id,
            @RequestParam(value = "password", required = false) String password
    ) {
        return fileService.download(id, password);
    }
}
