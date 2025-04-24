package org.example.progetto.repositories;

import org.example.progetto.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_Id(Integer productId);
    List<Review> findByUser_Id(Integer userId);
    List<Review> findByVote(Integer vote);
    List<Review> findByMessageContaining(String word);
}
