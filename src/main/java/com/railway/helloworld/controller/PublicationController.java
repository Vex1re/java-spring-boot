package com.railway.helloworld.controller;

import com.railway.helloworld.model.Publication;
import com.railway.helloworld.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PublicationController {
    private static final Logger logger = LoggerFactory.getLogger(PublicationController.class);

    @Autowired
    private PublicationService publicationService;

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

    @PostMapping
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

}
