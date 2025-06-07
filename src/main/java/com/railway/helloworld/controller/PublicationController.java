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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/posts")
public class PublicationController {
    private static final Logger logger = LoggerFactory.getLogger(PublicationController.class);

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
    public ResponseEntity<?> uploadImages(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        try {
            List<String> imageUrls = new ArrayList<>();
            
            for (MultipartFile file : files) {
                String imageUrl = fileStorageService.storeFile(file);
                imageUrls.add(imageUrl);
            }
            
            Publication updatedPost = publicationService.addImagesToPost(id, imageUrls);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            logger.error("Error uploading images: ", e);
            return ResponseEntity.internalServerError().body("Error uploading images: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<?> removeImage(@PathVariable Long id, @RequestParam String imageUrl) {
        try {
            Publication updatedPost = publicationService.removeImageFromPost(id, imageUrl);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            logger.error("Error removing image: ", e);
            return ResponseEntity.internalServerError().body("Error removing image: " + e.getMessage());
        }
    }
}
