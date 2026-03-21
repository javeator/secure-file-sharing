package com.example.fileshare;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class FileEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String originalName;
    private String storagePath;
    private long size;

    private LocalDateTime uploadTime;
    private LocalDateTime expireAt;

    private String passwordHash;

    private int downloadCount;
}