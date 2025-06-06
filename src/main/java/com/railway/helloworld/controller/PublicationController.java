package com.railway.helloworld.controller;

import com.railway.helloworld.model.Publication;
import com.railway.helloworld.service.PublicationService;
import com.railway.helloworld.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/posts")
public class PublicationController {
    private static final Logger logger = LoggerFactory.getLogger(PublicationController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPostById(@PathVariable long id) {
        return publicationService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public List<Publication> getPosts() {
        return publicationService.getAllPosts();
    }

    @PostMapping("/all")
    public ResponseEntity<?> createPost(@RequestBody Publication post) {
        try {
            logger.info("Creating new post: {}", post);
            Publication createdPost = publicationService.createPost(post);
            return ResponseEntity.ok(createdPost);
        } catch (Exception e) {
            logger.error("Error creating post: ", e);
            return ResponseEntity.internalServerError().body("Error creating post: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publication> updatePost(@PathVariable Long id, @RequestBody Publication postDetails) {
        try {
            Publication updatedPost = publicationService.updatePost(id, postDetails);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        publicationService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileStorageService.storeFile(file);
            String imageUrl = "/api/files/" + fileName;
            
            Publication post = publicationService.getPostById(id)
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

            List<String> images = new ArrayList<>();
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                images = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
            }
            images.add(imageUrl);
            post.setImages(objectMapper.writeValueAsString(images));
            
            Publication updatedPost = publicationService.updatePost(id, post);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            logger.error("Error uploading image: ", e);
            return ResponseEntity.internalServerError().body("Error uploading image: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<?> removeImage(@PathVariable Long id, @RequestParam String imageUrl) {
        try {
            Publication post = publicationService.getPostById(id)
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

            if (post.getImages() != null && !post.getImages().isEmpty()) {
                List<String> images = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
                images.remove(imageUrl);
                post.setImages(objectMapper.writeValueAsString(images));
                
                // Удаляем файл с сервера
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                fileStorageService.deleteFile(fileName);
                
                Publication updatedPost = publicationService.updatePost(id, post);
                return ResponseEntity.ok(updatedPost);
            }
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            logger.error("Error removing image: ", e);
            return ResponseEntity.internalServerError().body("Error removing image: " + e.getMessage());
        }
    }
}
