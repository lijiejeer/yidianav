package jbc.com.cn.yidianav.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {

    private static final String BACKUP_DIR = "backups";
    private static final String DB_FILE = "navigation.db";
    private static final String UPLOAD_DIR = "uploads";
    private static final int MAX_BACKUPS = 5;

    public String createBackup() throws IOException {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFileName = "backup_" + timestamp + ".zip";
        String backupFilePath = BACKUP_DIR + File.separator + backupFileName;

        try (FileOutputStream fos = new FileOutputStream(backupFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            if (new File(DB_FILE).exists()) {
                addToZip(DB_FILE, DB_FILE, zos);
            }

            File uploadDir = new File(UPLOAD_DIR);
            if (uploadDir.exists() && uploadDir.isDirectory()) {
                addDirectoryToZip(uploadDir, UPLOAD_DIR, zos);
            }
        }

        cleanOldBackups();

        return backupFileName;
    }

    private void addToZip(String filePath, String zipEntryName, ZipOutputStream zos) throws IOException {
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.closeEntry();
        }
    }

    private void addDirectoryToZip(File directory, String basePath, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirectoryToZip(file, basePath + File.separator + file.getName(), zos);
                } else {
                    addToZip(file.getPath(), basePath + File.separator + file.getName(), zos);
                }
            }
        }
    }

    public void restoreBackup(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("restore", ".zip");
        file.transferTo(tempFile);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File entryFile = new File(entry.getName());
                
                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    entryFile.getParentFile().mkdirs();
                    
                    try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        } finally {
            tempFile.delete();
        }
    }

    public List<Map<String, Object>> listBackups() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            return new ArrayList<>();
        }

        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".zip"));
        if (files == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
                .map(file -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("name", file.getName());
                    info.put("size", file.length());
                    info.put("date", new Date(file.lastModified()));
                    return info;
                })
                .sorted((a, b) -> ((Date) b.get("date")).compareTo((Date) a.get("date")))
                .collect(Collectors.toList());
    }

    private void cleanOldBackups() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            return;
        }

        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".zip"));
        if (files == null || files.length <= MAX_BACKUPS) {
            return;
        }

        Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        for (int i = MAX_BACKUPS; i < files.length; i++) {
            files[i].delete();
        }
    }

    public void scheduleAutoBackup(int days, int months) {
    }
}
