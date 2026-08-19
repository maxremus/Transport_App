package org.example.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.service.AuditLogService;
import org.example.adminservice.service.BackupService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;
    private final AuditLogService auditLogService;

    @GetMapping("/backup")
    public ResponseEntity<byte[]> downloadBackup() throws Exception {

        byte[] json = backupService.generateBackup();

        String filename = "backup-" +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) +
                ".json";

        auditLogService.log("DOWNLOAD_BACKUP", "filename=" + filename + ", size=" + json.length + " bytes");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(json);
    }
}
