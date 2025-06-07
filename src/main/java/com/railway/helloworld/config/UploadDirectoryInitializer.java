package com.railway.helloworld.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadDirectoryInitializer implements CommandLineRunner {

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Override
    public void run(String... args) throws Exception {
        Path uploadPath = Paths.get(fileStorageConfig.getUploadDir());
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
        }

        // Устанавливаем права доступа на директорию
        File uploadDir = uploadPath.toFile();
        uploadDir.setReadable(true, false);
        uploadDir.setWritable(true, false);
        uploadDir.setExecutable(true, false);
    }
} 