package com.example.fileshare;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {
}