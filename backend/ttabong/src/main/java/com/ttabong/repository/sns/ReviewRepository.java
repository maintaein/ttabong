package com.ttabong.repository.sns;

import com.ttabong.entity.sns.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

}