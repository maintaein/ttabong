package com.ttabong.repository.sns;

import com.ttabong.entity.sns.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r.recruit.template.id FROM Review r WHERE r.id = :reviewId")
    Integer findTemplateIdByReviewId(Integer reviewId);

}