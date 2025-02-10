package com.ttabong.repository.sns;

import com.ttabong.entity.sns.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.writer w
        LEFT JOIN FETCH r.org o
        LEFT JOIN FETCH r.recruit rec
        WHERE (:cursor IS NULL OR r.id < :cursor)
        AND r.isDeleted = false
        AND r.isPublic = true
        ORDER BY r.id DESC
    """)
    List<Review> findAllReviews(@Param("cursor") Integer cursor, Pageable pageable);

}
