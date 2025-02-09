package com.ttabong.repository.sns;

import com.ttabong.entity.sns.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {

        // 🔹 특정 템플릿 또는 리뷰의 이미지 가져오기
        List<ReviewImage> findByTemplateId(Integer templateId);
        List<ReviewImage> findByReviewId(Integer reviewId);

        // 🔹 특정 템플릿 또는 리뷰의 대표 이미지 찾기 (최초 등록된 이미지)
        Optional<ReviewImage> findFirstByTemplateIdOrderByIdAsc(Integer templateId);
        Optional<ReviewImage> findFirstByReviewIdOrderByIdAsc(Integer reviewId);

        // 🔹 특정 템플릿 또는 리뷰의 모든 대표 이미지 `isThumbnail = false`로 초기화
        List<ReviewImage> findByTemplateIdAndIsThumbnailTrue(Integer templateId);
        List<ReviewImage> findByReviewIdAndIsThumbnailTrue(Integer reviewId);

        // 🔹 특정 이미지 ID의 `isThumbnail`을 `true`로 변경
        Optional<ReviewImage> findById(Integer imageId);
}
