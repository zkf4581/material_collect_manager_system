package com.gongdi.materialpoints.modules.file.service;

import com.gongdi.materialpoints.config.FileStorageProperties;
import com.gongdi.materialpoints.modules.file.domain.FileRecord;
import com.gongdi.materialpoints.modules.file.repository.FileRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final FileStorageProperties fileStorageProperties;
    private final FileRecordRepository fileRecordRepository;

    public FileStorageService(FileStorageProperties fileStorageProperties, FileRecordRepository fileRecordRepository) {
        this.fileStorageProperties = fileStorageProperties;
        this.fileRecordRepository = fileRecordRepository;
    }

    public UploadResult store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("仅支持 jpg、png、webp 图片");
        }
        if (file.getSize() > 5 * 1024 * 1024L) {
            throw new IllegalArgumentException("图片大小不能超过 5MB");
        }

        LocalDate today = LocalDate.now();
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String safeExtension = extension == null ? "jpg" : extension.toLowerCase();
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + safeExtension;
        Path relativePath = Path.of(
                String.valueOf(today.getYear()),
                String.format("%02d", today.getMonthValue()),
                String.format("%02d", today.getDayOfMonth()),
                fileName
        );
        Path basePath = Path.of(fileStorageProperties.baseDir()).toAbsolutePath().normalize();
        Path targetPath = basePath.resolve(relativePath);
        Files.createDirectories(targetPath.getParent());
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        FileRecord record = new FileRecord();
        record.setOriginalName(file.getOriginalFilename());
        record.setFileUrl("/uploads/" + relativePath.toString().replace("\\", "/"));
        record.setStorageType(fileStorageProperties.storageType().toUpperCase());
        record.setMimeType(file.getContentType());
        record.setFileSize(file.getSize());
        FileRecord saved = fileRecordRepository.save(record);

        return new UploadResult(
                saved.getId(),
                saved.getFileUrl(),
                saved.getStorageType(),
                saved.getFileSize(),
                saved.getMimeType()
        );
    }

    public record UploadResult(
            Long fileId,
            String url,
            String storageType,
            Long size,
            String mime
    ) {
    }
}
