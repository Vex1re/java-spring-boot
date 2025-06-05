package com.railway.helloworld;

import com.railway.helloworld.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Publication, Long> {

}
