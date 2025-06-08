package com.railway.helloworld.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadDirectoryInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(UploadDirectoryInitializer.class);

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Override
    public void run(String... args) throws Exception {
        Path uploadPath = Paths.get(fileStorageConfig.getUploadDir());
        String absolutePath = uploadPath.toAbsolutePath().toString();
        
        logger.info("Initializing upload directory at: {}", absolutePath);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("Created upload directory: {}", absolutePath);
        } else {
            logger.info("Upload directory already exists: {}", absolutePath);
        }

        // Проверяем права доступа
        File uploadDir = uploadPath.toFile();
        boolean canRead = uploadDir.canRead();
        boolean canWrite = uploadDir.canWrite();
        boolean canExecute = uploadDir.canExecute();
        
        logger.info("Directory permissions - Read: {}, Write: {}, Execute: {}", 
                   canRead, canWrite, canExecute);

        // Устанавливаем права доступа
        uploadDir.setReadable(true, false);
        uploadDir.setWritable(true, false);
        uploadDir.setExecutable(true, false);
        
        logger.info("Directory permissions updated");
        
        // Проверяем свободное место
        long freeSpace = uploadDir.getFreeSpace();
        logger.info("Available space in upload directory: {} MB", freeSpace / (1024 * 1024));
    }
} 