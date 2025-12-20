package com.example.project.service.impl;

import com.example.project.service.FileStorageService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("documentStorage")
public class DocumentFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file, Long id) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File rỗng");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/jpg"))) {
            throw new RuntimeException("Chỉ cho phép JPG/PNG");
        }

        try {
            Path uploadPath = Paths.get(uploadDir,"cover/documents");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = "document_" + id + ".png";
            Path filePath = uploadPath.resolve(filename);
            Thumbnails.of(file.getInputStream())
                    .height(512)
                    .keepAspectRatio(true)
                    .allowOverwrite(true)
                    .outputFormat("png")
                    .outputQuality(1.0)
                    .toFile(filePath.toFile());

            return "/upload/cover/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file", e);
        }
    }
}
