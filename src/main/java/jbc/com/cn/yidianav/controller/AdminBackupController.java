package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/backup")
public class AdminBackupController {

    @Autowired
    private BackupService backupService;

    @PostMapping("/create")
    public ApiResponse<String> createBackup() {
        try {
            String filename = backupService.createBackup();
            return ApiResponse.success("Backup created successfully", filename);
        } catch (Exception e) {
            return ApiResponse.error("Failed to create backup: " + e.getMessage());
        }
    }

    @PostMapping("/restore")
    public ApiResponse<String> restoreBackup(@RequestParam("file") MultipartFile file) {
        try {
            backupService.restoreBackup(file);
            return ApiResponse.success("Backup restored successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to restore backup: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> listBackups() {
        return ApiResponse.success(backupService.listBackups());
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadBackup(@PathVariable String filename) {
        try {
            File file = new File("backups/" + filename);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{filename}")
    public ApiResponse<String> deleteBackup(@PathVariable String filename) {
        try {
            backupService.deleteBackup(filename);
            return ApiResponse.success("Backup deleted successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete backup: " + e.getMessage());
        }
    }

    @GetMapping("/auto-config")
    public ApiResponse<Map<String, Object>> getAutoBackupConfig() {
        return ApiResponse.success(backupService.getAutoBackupConfig());
    }

    @PostMapping("/auto-config")
    public ApiResponse<String> saveAutoBackupConfig(@RequestBody Map<String, Object> config) {
        try {
            boolean enabled = (Boolean) config.get("enabled");
            int days = ((Number) config.get("days")).intValue();
            int months = ((Number) config.get("months")).intValue();
            backupService.saveAutoBackupConfig(enabled, days, months);
            return ApiResponse.success("Auto backup configuration saved successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to save configuration: " + e.getMessage());
        }
    }

}
