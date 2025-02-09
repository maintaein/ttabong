package com.ttabong.util.service;

import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.sns.Review;
import com.ttabong.entity.sns.ReviewImage;
import com.ttabong.repository.recruit.TemplateRepository;
import com.ttabong.repository.sns.ReviewImageRepository;
import com.ttabong.repository.sns.ReviewRepository;
import com.ttabong.util.CacheUtil;
import com.ttabong.util.ImageUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final ReviewImageRepository reviewImageRepository;
    private final TemplateRepository templateRepository;
    private final ReviewRepository reviewRepository;
    private final CacheUtil cacheUtil;
    private final ImageUtil imageUtil;

    // 미리 슬롯 10개 받아두기
    @Override
    @Transactional
    public void initializeReviewImages(Integer entityId, boolean isTemplate) {
        for (int i = 0; i < 10; i++) {
            ReviewImage.ReviewImageBuilder imageBuilder = ReviewImage.builder()
                    .imageUrl(null)
                    .isThumbnail(i == 0)
                    .isDeleted(false)
                    .createdAt(Instant.now());

            if (isTemplate) {
                Template template = templateRepository.findById(entityId)
                        .orElseThrow(() -> new EntityNotFoundException("해당 템플릿이 존재하지 않습니다."));
                imageBuilder.template(template);
            } else {
                Review review = reviewRepository.findById(entityId)
                        .orElseThrow(() -> new EntityNotFoundException("해당 후기가 존재하지 않습니다."));

                Template template = review.getRecruit().getTemplate();
                imageBuilder.review(review);
                imageBuilder.template(template);
            }

            reviewImageRepository.save(imageBuilder.build());
        }
    }


    @Transactional
    public void updateReviewImages(Integer templateId, List<String> presignedUrls) {
        List<ReviewImage> reviewImages = reviewImageRepository.findByTemplateId(templateId);

        for (int i = 0; i < presignedUrls.size(); i++) {
            if (i >= reviewImages.size()) break;

            String objectPath = cacheUtil.findObjectPath(presignedUrls.get(i));

            if (objectPath == null) {
                continue;
            }

            reviewImages.get(i).setImageUrl(objectPath);
            reviewImages.get(i).setIsDeleted(false);
            reviewImages.get(i).setThumbnail(i == 0);
        }

        reviewImageRepository.saveAll(reviewImages);
    }


    @Override
    @Transactional
    public void updateThumbnailImage(Integer entityId, boolean isTemplate) {
        List<ReviewImage> thumbnails = isTemplate
                ? reviewImageRepository.findByTemplateIdAndIsThumbnailTrue(entityId)
                : reviewImageRepository.findByReviewIdAndIsThumbnailTrue(entityId);

        // 모든 기존 대표 이미지 초기화
        thumbnails.forEach(image -> image.setThumbnail(false));
        reviewImageRepository.saveAll(thumbnails);

        // 새로운 대표 이미지 설정
        Optional<ReviewImage> firstImage = isTemplate
                ? reviewImageRepository.findFirstByTemplateIdOrderByIdAsc(entityId)
                : reviewImageRepository.findFirstByReviewIdOrderByIdAsc(entityId);

        firstImage.ifPresent(image -> {
            image.setThumbnail(true);
            reviewImageRepository.save(image);
        });
    }


    public List<String> getImageUrls(Integer entityId, boolean isTemplate) {
        return (isTemplate ? reviewImageRepository.findByTemplateId(entityId)
                : reviewImageRepository.findByReviewId(entityId))
                .stream()
                .filter(image -> !image.getIsDeleted()) // 삭제된 이미지 제외
                .map(ReviewImage::getImageUrl)
                .filter(Objects::nonNull)
                .map(imageUrl -> {
                    try {
                        return imageUtil.getPresignedDownloadUrl(imageUrl);
                    } catch (Exception e) {
                        throw new RuntimeException("MinIO Presigned URL 생성 중 오류 발생", e);
                    }
                })
                .collect(Collectors.toList());
    }

}
