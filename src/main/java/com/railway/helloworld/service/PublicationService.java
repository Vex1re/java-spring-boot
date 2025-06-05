package com.railway.helloworld.service;

import com.railway.helloworld.PostRepository;
import com.railway.helloworld.model.Publication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class PublicationService {
    private static final Logger logger = LoggerFactory.getLogger(PublicationService.class);

    @Autowired
    private PostRepository postRepository;

    public List<Publication> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Publication> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Publication createPost(Publication post) {
        try {
            logger.info("Creating post with data: {}", post);
            
            // Инициализация значений по умолчанию
            if (post.getCommentsCount() == null) {
                post.setCommentsCount(0);
            }
            if (post.getRating() == 0) {
                post.setRating(0);
            }
            
            Publication savedPost = postRepository.save(post);
            logger.info("Successfully created post with id: {}", savedPost.getId());
            return savedPost;
        } catch (Exception e) {
            logger.error("Error creating post: ", e);
            throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
        }
    }

    public Publication updatePost(Long id, Publication postDetails) {
        Publication post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setName(postDetails.getName());
        post.setPlaceName(postDetails.getPlaceName());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}