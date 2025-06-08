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
        // Создаем основную директорию для загрузок
        Path uploadPath = Paths.get(fileStorageConfig.getUploadDir());
        String absolutePath = uploadPath.toAbsolutePath().toString();
        
        logger.info("Initializing upload directory at: {}", absolutePath);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("Created upload directory: {}", absolutePath);
        } else {
            logger.info("Upload directory already exists: {}", absolutePath);
        }

        // Проверяем права доступа для директории
        setDirectoryPermissions(uploadPath.toFile());
        
        // Проверяем свободное место
        long freeSpace = uploadPath.toFile().getFreeSpace();
        logger.info("Available space in upload directory: {} MB", freeSpace / (1024 * 1024));
    }
    
    private void setDirectoryPermissions(File directory) {
        boolean canRead = directory.canRead();
        boolean canWrite = directory.canWrite();
        boolean canExecute = directory.canExecute();
        
        logger.info("Directory {} permissions - Read: {}, Write: {}, Execute: {}", 
                   directory.getAbsolutePath(), canRead, canWrite, canExecute);

        // Устанавливаем права доступа
        directory.setReadable(true, false);
        directory.setWritable(true, false);
        directory.setExecutable(true, false);
        
        logger.info("Directory {} permissions updated", directory.getAbsolutePath());
    }
} 