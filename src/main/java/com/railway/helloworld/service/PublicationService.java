package com.railway.helloworld.service;

import com.railway.helloworld.PostRepository;
import com.railway.helloworld.model.Publication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class PublicationService {
    private static final Logger logger = LoggerFactory.getLogger(PublicationService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            if (post.getImages() == null) {
                post.setImages("[]");
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
        
        // Сохраняем все поля, проверяя на null
        if (postDetails.getName() != null) {
            post.setName(postDetails.getName());
        }
        if (postDetails.getPlaceName() != null) {
            post.setPlaceName(postDetails.getPlaceName());
        }
        if (postDetails.getDescription() != null) {
            post.setDescription(postDetails.getDescription());
        }
        if (postDetails.getLocation() != null) {
            post.setLocation(postDetails.getLocation());
        }
        if (postDetails.getTime() != null) {
            post.setTime(postDetails.getTime());
        }
        if (postDetails.getTag() != null) {
            post.setTag(postDetails.getTag());
        }
        if (postDetails.getLogin() != null) {
            post.setLogin(postDetails.getLogin());
        }
        if (postDetails.getImages() != null) {
            post.setImages(postDetails.getImages());
        }
        
        // Для числовых полей проверяем на 0
        if (postDetails.getRating() != 0) {
            post.setRating(postDetails.getRating());
        }
        if (postDetails.getCommentsCount() != null && postDetails.getCommentsCount() != 0) {
            post.setCommentsCount(postDetails.getCommentsCount());
        }
        
        logger.info("Updating post with id: {}, new data: {}", id, post);
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Publication addImagesToPost(Long postId, List<String> imageUrls) {
        Publication post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        try {
            List<String> images = new ArrayList<>();
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                images = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
            }
            images.addAll(imageUrls);
            post.setImages(objectMapper.writeValueAsString(images));
            return postRepository.save(post);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add images to post: " + e.getMessage(), e);
        }
    }

    public Publication removeImageFromPost(Long postId, String imageUrl) {
        Publication post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        try {
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                List<String> images = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
                images.remove(imageUrl);
                post.setImages(objectMapper.writeValueAsString(images));
                return postRepository.save(post);
            }
            return post;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove image from post: " + e.getMessage(), e);
        }
    }

    public Publication addLikeToPost(Long postId, String userLogin) {
        Publication post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        try {
            List<String> likes = new ArrayList<>();
            String currentLikes = post.getLikes();
            
            if (currentLikes != null && !currentLikes.isEmpty()) {
                try {
                    likes = objectMapper.readValue(currentLikes, new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    logger.warn("Failed to parse likes JSON, initializing empty list. Error: {}", e.getMessage());
                }
            }
            
            // Проверяем, есть ли уже лайк от этого пользователя
            boolean userLiked = likes.stream()
                    .anyMatch(like -> like.startsWith(userLogin + ":"));
            
            if (!userLiked) {
                likes.add(userLogin + ":true");
                String newLikes = objectMapper.writeValueAsString(likes);
                logger.info("Adding like for user {} to post {}. New likes: {}", userLogin, postId, newLikes);
                post.setLikes(newLikes);
                return postRepository.save(post);
            }
            
            logger.info("User {} already liked post {}", userLogin, postId);
            return post;
        } catch (Exception e) {
            logger.error("Failed to add like to post: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add like to post: " + e.getMessage(), e);
        }
    }

    public Publication removeLikeFromPost(Long postId, String userLogin) {
        Publication post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        try {
            String currentLikes = post.getLikes();
            if (currentLikes != null && !currentLikes.isEmpty()) {
                List<String> likes;
                try {
                    likes = objectMapper.readValue(currentLikes, new TypeReference<List<String>>() {});
                    // Удаляем лайк пользователя
                    likes = likes.stream()
                            .filter(like -> !like.startsWith(userLogin + ":"))
                            .collect(Collectors.toList());
                    
                    String newLikes = objectMapper.writeValueAsString(likes);
                    logger.info("Removing like for user {} from post {}. New likes: {}", userLogin, postId, newLikes);
                    post.setLikes(newLikes);
                    return postRepository.save(post);
                } catch (Exception e) {
                    logger.warn("Failed to parse likes JSON, skipping removal. Error: {}", e.getMessage());
                }
            }
            
            logger.info("User {} hasn't liked post {}", userLogin, postId);
            return post;
        } catch (Exception e) {
            logger.error("Failed to remove like from post: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to remove like from post: " + e.getMessage(), e);
        }
    }

    public boolean hasUserLikedPost(Long postId, String userLogin) {
        Publication post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        try {
            String currentLikes = post.getLikes();
            if (currentLikes != null && !currentLikes.isEmpty()) {
                try {
                    List<String> likes = objectMapper.readValue(currentLikes, new TypeReference<List<String>>() {});
                    boolean hasLiked = likes.stream()
                            .anyMatch(like -> like.startsWith(userLogin + ":") && like.endsWith(":true"));
                    logger.info("Checking like for user {} on post {}. Result: {}", userLogin, postId, hasLiked);
                    return hasLiked;
                } catch (Exception e) {
                    logger.warn("Failed to parse likes JSON, returning false. Error: {}", e.getMessage());
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("Failed to check if user liked post: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check if user liked post: " + e.getMessage(), e);
        }
    }
}