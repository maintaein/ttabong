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
        LEFT JOIN FETCH rec.template t
        LEFT JOIN FETCH t.group g
        WHERE (:cursor IS NULL OR r.id < :cursor)
        AND r.isDeleted = false
        ORDER BY r.id DESC
    """)
    List<Review> findAllReviews(@Param("cursor") Integer cursor, Pageable pageable);

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.writer w
        LEFT JOIN FETCH r.org o
        LEFT JOIN FETCH r.recruit rec
        LEFT JOIN FETCH rec.template t
        LEFT JOIN FETCH t.group g
        WHERE w.id = :writerId
        AND r.isDeleted = false
        ORDER BY r.id DESC
    """)
    List<Review> findMyReviews(@Param("writerId") Integer writerId, Pageable pageable);

    @Query("""
        SELECT r FROM Review r
        LEFT JOIN FETCH r.writer w
        LEFT JOIN FETCH r.org o
        LEFT JOIN FETCH r.recruit rec
        LEFT JOIN FETCH rec.template t
        LEFT JOIN FETCH t.group g
        WHERE rec.id = :recruitId
        AND r.isDeleted = false
        ORDER BY r.createdAt DESC
    """)
    List<Review> findByRecruitId(@Param("recruitId") Integer recruitId);


}
