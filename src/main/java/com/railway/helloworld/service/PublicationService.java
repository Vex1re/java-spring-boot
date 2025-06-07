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
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.railway.helloworld.callback.OnNetworkCallback;
import com.railway.helloworld.model.Post;
import com.railway.helloworld.model.PostRating;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Service
public class PublicationService {
    private static final Logger logger = LoggerFactory.getLogger(PublicationService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RestTemplate restTemplate;

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
        
        logger.info("Starting post update for id: {}", id);
        logger.info("Current post state: {}", post);
        logger.info("Update details: {}", postDetails);
        
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
        logger.info("Current rating: {}", post.getRating());
        logger.info("New rating value: {}", postDetails.getRating());
        
        // Убираем проверку на 0 для рейтинга
        post.setRating(postDetails.getRating());
        logger.info("Rating after update: {}", post.getRating());
        
        if (postDetails.getCommentsCount() != null && postDetails.getCommentsCount() != 0) {
            post.setCommentsCount(postDetails.getCommentsCount());
        }
        
        logger.info("Final post state before save: {}", post);
        Publication savedPost = postRepository.save(post);
        logger.info("Saved post state: {}", savedPost);
        return savedPost;
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

    public void updatePostRating(PostRating postRating, OnNetworkCallback callback) {
        if (postRating == null) {
            if (callback != null) {
                callback.onError("Ошибка: postRating не может быть null");
            }
            return;
        }
        
        try {
            ResponseEntity<Post> response = restTemplate.postForEntity(
                "/api/posts/{postId}/rating",
                postRating,
                Post.class,
                postRating.getPostId()
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Рейтинг поста успешно обновлен");
                getPosts(callback);
            } else {
                handleErrorResponse(response, callback);
            }
        } catch (Exception e) {
            logger.error("Ошибка при обновлении рейтинга поста", e);
            if (callback != null) {
                callback.onError("Ошибка при обновлении рейтинга: " + e.getMessage());
            }
        }
    }
    
    private void handleErrorResponse(ResponseEntity<Post> response, OnNetworkCallback callback) {
        String errorMessage = String.format("Ошибка при обновлении рейтинга (код: %d)", 
            response.getStatusCode().value());
        logger.error(errorMessage);
        
        if (callback != null) {
            callback.onError(errorMessage);
        }
    }
    
    public void getPosts(OnNetworkCallback callback) {
        try {
            ResponseEntity<Post[]> response = restTemplate.getForEntity("/api/posts", Post[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                callback.onSuccess(response.getBody());
            } else {
                callback.onError("Ошибка при получении списка постов");
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении списка постов", e);
            callback.onError("Ошибка при получении списка постов: " + e.getMessage());
        }
    }
}