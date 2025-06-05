package com.railway.helloworld.service;

import com.railway.helloworld.PostRepository;
import com.railway.helloworld.model.Publication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublicationService {

    @Autowired
    private PostRepository postRepository;

    public List<Publication> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Publication> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Publication createPost(Publication post) {
        return postRepository.save(post);
    }

    public Publication updatePost(Long id, Publication postDetails) {
        Publication post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setName(postDetails.getName());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

}
