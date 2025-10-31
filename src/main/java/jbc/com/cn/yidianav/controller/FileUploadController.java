package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadFile(file);
            return ApiResponse.success("File uploaded successfully", url);
        } catch (Exception e) {
            return ApiResponse.error("Failed to upload file: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ApiResponse<String> deleteFile(@RequestParam String url) {
        if (fileUploadService.deleteFile(url)) {
            return ApiResponse.success("File deleted successfully");
        }
        return ApiResponse.error("Failed to delete file");
    }
}
