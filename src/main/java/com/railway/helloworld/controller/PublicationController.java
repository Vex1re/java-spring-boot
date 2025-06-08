package com.railway.helloworld.controller;

import com.railway.helloworld.model.Publication;
import com.railway.helloworld.service.PublicationService;
import com.railway.helloworld.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @PutMapping("/{id}/rating")
    public ResponseEntity<?> updateRating(@PathVariable Long id, @RequestBody Map<String, Integer> ratingData) {
        try {
            Integer newRating = ratingData.get("rating");
            if (newRating == null) {
                return ResponseEntity.badRequest().body("Rating is required");
            }

            Publication post = publicationService.getPostById(id)
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
            
            post.setRating(newRating);
            Publication updatedPost = publicationService.updatePost(id, post);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            logger.error("Error updating rating: ", e);
            return ResponseEntity.internalServerError().body("Error updating rating: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        publicationService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable Long id,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "items", required = false) String items,
            @RequestPart(value = "isAny", required = false) String isAny) {
        try {
            logger.info("Received upload request for post id: {}, files count: {}", id, files != null ? files.length : 0);
            
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().body("No files provided");
            }

            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = fileStorageService.storeFile(file);
                    imageUrls.add(imageUrl);
                    logger.info("Successfully stored file: {}", imageUrl);
                }
            }
            
            Publication updatedPost = publicationService.addImagesToPost(id, imageUrls);
            
            Map<String, Object> response = new HashMap<>();
            response.put("post", updatedPost);
            response.put("uploadedFiles", imageUrls);
            
            return ResponseEntity.ok(response);
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

    @GetMapping("/{id}/likes/check")
    public ResponseEntity<?> checkLike(
            @PathVariable Long id,
            @RequestParam(required = true) String userLogin) {
        try {
            if (userLogin == null || userLogin.trim().isEmpty()) {
                logger.warn("Attempt to check like with empty userLogin for post {}", id);
                return ResponseEntity.badRequest().body("User login is required");
            }

            logger.info("Checking like for user {} on post {}", userLogin, id);
            boolean hasLiked = publicationService.hasUserLikedPost(id, userLogin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasLiked", hasLiked);
            response.put("postId", id);
            response.put("userLogin", userLogin);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking like: ", e);
            return ResponseEntity.internalServerError().body("Error checking like: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> addLike(@PathVariable Long id, @RequestBody Map<String, String> userData) {
        try {
            String userLogin = userData.get("userLogin");
            if (userLogin == null || userLogin.trim().isEmpty()) {
                logger.warn("Attempt to add like with empty userLogin for post {}", id);
                return ResponseEntity.badRequest().body("User login is required");
            }

            logger.info("Adding like for user {} to post {}", userLogin, id);
            Publication updatedPost = publicationService.addLikeToPost(id, userLogin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("post", updatedPost);
            response.put("likes", updatedPost.getLikes());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding like: ", e);
            return ResponseEntity.internalServerError().body("Error adding like: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> removeLike(@PathVariable Long id, @RequestBody Map<String, String> userData) {
        try {
            String userLogin = userData.get("userLogin");
            if (userLogin == null || userLogin.trim().isEmpty()) {
                logger.warn("Attempt to remove like with empty userLogin for post {}", id);
                return ResponseEntity.badRequest().body("User login is required");
            }

            logger.info("Removing like for user {} from post {}", userLogin, id);
            Publication updatedPost = publicationService.removeLikeFromPost(id, userLogin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("post", updatedPost);
            response.put("likes", updatedPost.getLikes());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error removing like: ", e);
            return ResponseEntity.internalServerError().body("Error removing like: " + e.getMessage());
        }
    }
}
