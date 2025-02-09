package com.ttabong.service.sns;

import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.response.ReviewCreateResponseDto;
import com.ttabong.dto.sns.response.ReviewDeleteResponseDto;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.sns.Review;
import com.ttabong.entity.sns.ReviewImage;
import com.ttabong.entity.user.Organization;
import com.ttabong.entity.user.User;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.repository.recruit.TemplateRepository;
import com.ttabong.repository.sns.ReviewImageRepository;
import com.ttabong.repository.sns.ReviewRepository;
import com.ttabong.repository.user.OrganizationRepository;
import com.ttabong.repository.user.UserRepository;
import com.ttabong.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;
    private final TemplateRepository templateRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final CacheUtil cacheUtil;

    // TODO: parent-review-id 설정하는거 해야함.
    @Transactional
    @Override
    public ReviewCreateResponseDto createReview(ReviewCreateRequestDto requestDto) {
        final Organization organization = organizationRepository.findById(requestDto.getOrgId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        final User writer = userRepository.findById(requestDto.getWriterId())
                .orElseThrow(() -> new RuntimeException("Writer not found"));

        final Recruit recruit = recruitRepository.findById(requestDto.getRecruitId())
                .orElseThrow(() -> new RuntimeException("Recruit not found"));

        // recruit_id로 template_id 조회
        final Template template = templateRepository.findById(recruit.getTemplate().getId())
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // template_id로 group_id 조회
        final Integer groupId = template.getGroup().getId();

        final Review review = Review.builder()
                .recruit(recruit)
                .org(organization)
                .writer(writer)
                .groupId(groupId)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .isPublic(requestDto.getIsPublic())
                .imgCount(requestDto.getImageCount())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        reviewRepository.save(review);

        // 1: 미리 10개의 이미지 슬롯 생성 (초기화)
        List<ReviewImage> imageSlots = IntStream.range(0, 10)
                .mapToObj(i -> ReviewImage.builder()
                        .review(review)
                        .template(template)
                        .imageUrl(null)  // Presigned URL이 아니라, 객체명을 저장할 예정
                        .isThumbnail(i == 0)  // 첫 번째 이미지만 썸네일로 설정
                        .isDeleted(false)
                        .createdAt(Instant.now())
                        .build())
                .collect(Collectors.toList());
        reviewImageRepository.saveAll(imageSlots); // 미리 저장

        // 2.Presigned URL을 기반으로 실제 객체명(objectPath) 업데이트
        final List<String> uploadedImages = requestDto.getUploadedImages();
        IntStream.range(0, uploadedImages.size()).forEach(i -> {
            final String objectPath = cacheUtil.findObjectPath(uploadedImages.get(i));
            if (objectPath == null) {
                throw new RuntimeException("Invalid or expired presigned URL");
            }

            // 기존 슬롯 업데이트 (Presigned URL이 아니라, objectPath를 저장)
            ReviewImage imageSlot = imageSlots.get(i);
            imageSlot.setImageUrl(objectPath);
            imageSlot.setThumbnail(i == 0);
            reviewImageRepository.save(imageSlot);
        });

        return ReviewCreateResponseDto.builder()
                .message("Review created successfully")
                .reviewId(review.getId())
                .writerId(writer.getId())
                .uploadedImages(requestDto.getUploadedImages())  // 그대로 반환 (objectPath 아님)
                .build();
    }


    /* 삭제 */
    @Transactional
    @Override
    public ReviewDeleteResponseDto deleteReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 후기를 찾을 수 없습니다. ID: " + reviewId));

        Review updatedReview = review.toBuilder()
                .isDeleted(true)
                .build();

        reviewRepository.save(updatedReview);

        return new ReviewDeleteResponseDto("삭제 성공하였습니다.", updatedReview.getId(), updatedReview.getTitle(), updatedReview.getContent());
    }
}
