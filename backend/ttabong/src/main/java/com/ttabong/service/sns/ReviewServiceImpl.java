package com.ttabong.service.sns;

import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.request.ReviewEditRequestDto;
import com.ttabong.dto.sns.request.ReviewVisibilitySettingRequestDto;
import com.ttabong.dto.sns.response.*;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.entity.recruit.Category;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.recruit.TemplateGroup;
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
import com.ttabong.util.ImageUtil;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;
    private final TemplateRepository templateRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final CacheUtil cacheUtil;
    private final ImageUtil imageUtil;
    private final MinioClient minioClient;

    // TODO: parent-review-id 설정하는거 해야함.
    @Override
    public ReviewCreateResponseDto createReview(AuthDto authDto, ReviewCreateRequestDto requestDto) {

        if (authDto == null || authDto.getUserId() == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }

        final Organization organization = organizationRepository.findById(requestDto.getOrgId())
                .orElseThrow(() -> new RuntimeException("기관 없음"));

        final User writer = userRepository.findById(authDto.getUserId())
                .orElseThrow(() -> new RuntimeException("작성자 없음"));

        final Recruit recruit = recruitRepository.findById(requestDto.getRecruitId())
                .orElseThrow(() -> new RuntimeException("해당 공고 없음"));

        final Template template = templateRepository.findById(recruit.getTemplate().getId())
                .orElseThrow(() -> new RuntimeException("해당 템플릿 없음"));

        final Integer groupId = template.getGroup().getId();

        List<Review> parentReviews = reviewRepository.findByOrgWriterAndRecruit(recruit.getId());
        final Review parentReview = parentReviews.isEmpty() ? null : parentReviews.get(0);

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
                .parentReview(parentReview)
                .isDeleted(false)
                .build();

        reviewRepository.save(review);
        
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
        
        // 이미지 업로드하기
        uploadImagesToMinio(requestDto.getUploadedImages(), imageSlots);

        return ReviewCreateResponseDto.builder()
                .message("리뷰가 생성되었습니다.")
                .uploadedImages(requestDto.getUploadedImages())  // 그대로 반환 (objectPath 아님)
                .build();
    }

    public void uploadImagesToMinio(List<String> uploadedImages, List<ReviewImage> imageSlots) {

        if (uploadedImages == null || uploadedImages.isEmpty()) {
            return;
        }

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
    }


    @Override
    public ReviewEditStartResponseDto startReviewEdit(Integer reviewId, AuthDto authDto) {

        if (authDto == null || authDto.getUserId() == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰가 없습니다"));

        // 2️.기존 업로드된 이미지 가져오기
        List<String> existingImages = reviewImageRepository.findByReviewId(reviewId).stream()
                .filter(image -> image.getImageUrl() != null)
                .map(image -> {
                    try {
                        return imageUtil.getPresignedDownloadUrl(image.getImageUrl());
                    } catch (Exception e) {
                        throw new RuntimeException("presigned URL 생성에 실패", e);
                    }
                })
                .collect(Collectors.toList());

        // 3️. 새로 업로드할 Presigned URL 10개 생성
        List<String> newPresignedUrls = IntStream.range(0, 10)
                .mapToObj(i -> {
                    String objectName = "review-images/" + UUID.randomUUID() + ".webp";
                    String presignedUrl = imageUtil.getPresignedUploadUrl(objectName);
                    cacheUtil.mapTempPresignedUrlwithObjectPath(presignedUrl, objectName);
                    return presignedUrl;
                })
                .collect(Collectors.toList());

        // 4️. cacheId 발급 (랜덤 UUID 사용)
        Integer cacheId = UUID.randomUUID().hashCode();

        return ReviewEditStartResponseDto.builder()
                .cacheId(cacheId)
                .writerId(review.getWriter().getId())
                .title(review.getTitle())
                .content(review.getContent())
                .isPublic(review.getIsPublic())
                .getImages(existingImages)
                .presignedUrl(newPresignedUrls)
                .build();
    }

    @Override
    public ReviewEditResponseDto updateReview(Integer reviewId, ReviewEditRequestDto requestDto, AuthDto authDto) {
        if (authDto == null || authDto.getUserId() == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // 기존 후기 정보 업데이트
        Review updatedReview = review.toBuilder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .isPublic(requestDto.getIsPublic())
                .updatedAt(Instant.now())
                .build();
        reviewRepository.save(updatedReview);

        // 기존 10개의 `ReviewImage` 데이터 가져오기 (순서 보장_id순서대로 하기)
        List<ReviewImage> existingImages = reviewImageRepository.findByReviewIdOrderByIdAsc(reviewId);

        // 기존 이미지가 없을 경우, 10개 슬롯 자동 생성
        if (existingImages.isEmpty()) {
            existingImages = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> ReviewImage.builder()
                            .review(review)
                            .imageUrl(null)  // 기본값은 null
                            .isDeleted(false)
                            .createdAt(Instant.now())
                            .build())
                    .collect(Collectors.toList());

            reviewImageRepository.saveAll(existingImages);
        }

        // 새로운 Presigned URL 리스트 (최대 10개 제한)
        List<String> presignedUrls = requestDto.getPresignedUrl();
        int newSize = Math.min(presignedUrls.size(), 10);  // 최대 10개까지만 허용

        // 기존 `ReviewImage` 슬롯을 재사용하여 업데이트
        for (int i = 0; i < 10; i++) {
            ReviewImage imageSlot = existingImages.get(i);

            if (i < newSize) {  // 새로운 이미지로 업데이트
                String objectPath = cacheUtil.findObjectPath(presignedUrls.get(i));
                if (objectPath == null) {
                    throw new RuntimeException("유효하지 않은 presigned url 입니다");
                }

                // 기존 MinIO 파일 삭제 후 새로운 파일로 교체
                try {
                    if (imageSlot.getImageUrl() != null) { // 기존 이미지가 있을 경우 삭제
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket("ttabong-bucket")
                                        .object(imageSlot.getImageUrl())
                                        .build()
                        );
                    }
                } catch (Exception e) {
                    throw new RuntimeException("미니오에서 기존 이미지 삭제 실패", e);
                }

                // imageUrl 업데이트
                imageSlot = imageSlot.toBuilder()
                        .imageUrl(objectPath)
                        .isDeleted(false)
                        .build();
            } else {  // 새로운 이미지가 없는 경우, 기존 슬롯 초기화
                imageSlot = imageSlot.toBuilder()
                        .imageUrl(null)  // 기존 이미지 경로 제거
                        .isDeleted(false)  // 여전히 사용 가능하도록 유지
                        .build();
            }
            reviewImageRepository.save(imageSlot);
        }

        return ReviewEditResponseDto.builder()
                .cacheId(requestDto.getCacheId())
                .title(updatedReview.getTitle())
                .content(updatedReview.getContent())
                .isPublic(updatedReview.getIsPublic())
                .imageCount(newSize)
                .build();
    }

    @Override
    public ReviewDeleteResponseDto deleteReview(Integer reviewId, AuthDto authDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("해당 후기를 찾을 수 없습니다. reviewId: " + reviewId));

        if (!review.getWriter().getId().equals(authDto.getUserId())) {
            throw new SecurityException("본인이 작성한 후기만 삭제할 수 있습니다.");
        }

        Review updatedReview = review.toBuilder()
                .isDeleted(true)
                .build();

        reviewRepository.save(updatedReview);

        return new ReviewDeleteResponseDto("삭제 성공하였습니다.", updatedReview.getId(), updatedReview.getTitle(), updatedReview.getContent());
    }

    @Override
    public ReviewVisibilitySettingResponseDto updateVisibility(Integer reviewId,
                                                               ReviewVisibilitySettingRequestDto requestDto,
                                                               AuthDto authDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 후기를 찾을 수 없습니다. id: " + reviewId));

        if (!review.getWriter().getId().equals(authDto.getUserId())) {
            throw new SecurityException("본인이 작성한 후기만 수정할 수 있습니다.");
        }

        Review updatedReviewVisibility = review.toBuilder()
                .isPublic(!review.getIsPublic())
                .updatedAt(Instant.now())
                .build();

        reviewRepository.save(updatedReviewVisibility);

        LocalDateTime updateTime = updatedReviewVisibility.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        return new ReviewVisibilitySettingResponseDto(
                "공개 여부를 수정했습니다",
                reviewId,
                updatedReviewVisibility.getIsPublic(),
                updateTime
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllReviewPreviewResponseDto> readAllReviews(Integer cursor, Integer limit) {
        List<Review> reviews = reviewRepository.findAllReviews(cursor, PageRequest.of(0, limit));

        return reviews.stream()
                .map(review -> AllReviewPreviewResponseDto.builder()
                        .review(AllReviewPreviewResponseDto.ReviewDto.builder()
                                .reviewId(review.getId())
                                .recruitId(review.getRecruit() != null ? review.getRecruit().getId() : null)
                                .title(review.getTitle())
                                .content(review.getContent())
                                .isDeleted(review.getIsDeleted())
                                .updatedAt(review.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()) // Instant → LocalDateTime 변환
                                .createdAt(review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()) // Instant → LocalDateTime 변환
                                .build())
                        .writer(review.getWriter() != null ? AllReviewPreviewResponseDto.WriterDto.builder()
                                .writerId(review.getWriter().getId())
                                .name(review.getWriter().getName())
                                .build() : null)
                        .group(review.getRecruit() != null && review.getRecruit().getTemplate() != null &&
                                review.getRecruit().getTemplate().getGroup() != null ?
                                AllReviewPreviewResponseDto.GroupDto.builder()
                                        .groupId(review.getRecruit().getTemplate().getGroup().getId())
                                        .groupName(review.getRecruit().getTemplate().getGroup().getGroupName())
                                        .build()
                                : null)
                        .organization(AllReviewPreviewResponseDto.OrganizationDto.builder()
                                .orgId(review.getOrg().getId())
                                .orgName(review.getOrg().getOrgName())
                                .build())
                        .images(
                                reviewImageRepository.findThumbnailImageByReviewId(review.getId()).orElse(null)
                        )
                        .build())
                .collect(Collectors.toList());
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<MyAllReviewPreviewResponseDto> readMyAllReviews(AuthDto authDto) {
//        List<Review> reviews = reviewRepository.findMyReviews(authDto.getUserId(), PageRequest.of(0, 10));
//
//        return reviews.stream()
//                .map(review -> MyAllReviewPreviewResponseDto.builder()
//                        .review(MyAllReviewPreviewResponseDto.ReviewDto.builder()
//                                .reviewId(review.getId())
//                                .recruitId(review.getRecruit() != null ? review.getRecruit().getId() : null)
//                                .title(review.getTitle())
//                                .content(review.getContent())
//                                .isDeleted(review.getIsDeleted())
//                                .updatedAt(review.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
//                                .createdAt(review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
//
//                                .build()
//                        )
//                        .group(MyAllReviewPreviewResponseDto.GroupDto.builder()
//                                .groupId(review.getRecruit() != null && review.getRecruit().getTemplate() != null &&
//                                        review.getRecruit().getTemplate().getGroup() != null ?
//                                        review.getRecruit().getTemplate().getGroup().getId() : null)
//                                .groupName(review.getRecruit() != null && review.getRecruit().getTemplate() != null &&
//                                        review.getRecruit().getTemplate().getGroup() != null ?
//                                        review.getRecruit().getTemplate().getGroup().getGroupName() : "N/A")
//                                .build()
//                        )
//                        .organization(MyAllReviewPreviewResponseDto.OrganizationDto.builder()
//                                .orgId(review.getOrg() != null ? review.getOrg().getId() : null)
//                                .orgName(review.getOrg() != null ? review.getOrg().getOrgName() : "N/A")
//                                .build()
//                        )
//                        .images(
//                                reviewImageRepository.findThumbnailImageByReviewId(review.getId()).orElse(null)
//                        )
//                        .build())
//                .collect(Collectors.toList());
//    }

    @Override
    @Transactional(readOnly = true)
    public List<MyAllReviewPreviewResponseDto> readMyAllReviews(AuthDto authDto) {
        List<Review> reviews = reviewRepository.findMyReviews(authDto.getUserId(), PageRequest.of(0, 10));

        return reviews.stream()
                .map(review -> {
                    // 1. DB에서 저장된 이미지의 Object Path 가져오기
                    String objectPath = reviewImageRepository.findThumbnailImageByReviewId(review.getId()).orElse(null);

                    // 2. Presigned URL 변환 (Object Path -> Presigned URL)
                    String presignedUrl = null;
                    try {
                        presignedUrl = (objectPath != null) ? imageUtil.getPresignedDownloadUrl(objectPath) : null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    return MyAllReviewPreviewResponseDto.builder()
                            .review(MyAllReviewPreviewResponseDto.ReviewDto.builder()
                                    .reviewId(review.getId())
                                    .recruitId(review.getRecruit() != null ? review.getRecruit().getId() : null)
                                    .title(review.getTitle())
                                    .content(review.getContent())
                                    .isDeleted(review.getIsDeleted())
                                    .updatedAt(review.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                                    .createdAt(review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                                    .build()
                            )
                            .group(MyAllReviewPreviewResponseDto.GroupDto.builder()
                                    .groupId(review.getRecruit() != null && review.getRecruit().getTemplate() != null &&
                                            review.getRecruit().getTemplate().getGroup() != null ?
                                            review.getRecruit().getTemplate().getGroup().getId() : null)
                                    .groupName(review.getRecruit() != null && review.getRecruit().getTemplate() != null &&
                                            review.getRecruit().getTemplate().getGroup() != null ?
                                            review.getRecruit().getTemplate().getGroup().getGroupName() : "N/A")
                                    .build()
                            )
                            .organization(MyAllReviewPreviewResponseDto.OrganizationDto.builder()
                                    .orgId(review.getOrg() != null ? review.getOrg().getId() : null)
                                    .orgName(review.getOrg() != null ? review.getOrg().getOrgName() : "N/A")
                                    .build()
                            )
                            .images(presignedUrl) // Presigned URL 반환
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDetailResponseDto detailReview(Integer reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("해당 후기를 찾을 수 없습니다. reviewId: " + reviewId));

        User writer = review.getWriter();
        Recruit recruit = review.getRecruit();
        Template template = (recruit != null) ? recruit.getTemplate() : null;
        TemplateGroup group = (template != null) ? template.getGroup() : null;
        Category category = (template != null) ? template.getCategory() : null;
        Organization organization = review.getOrg();

        // 1. 리뷰의 모든 이미지 Object Path 가져오기
        List<String> objectPaths = reviewImageRepository.findAllImagesByReviewId(review.getId())
                .stream()
                .filter(Objects::nonNull) // null 값 제거
                .toList();

        // 2. Presigned URL 변환 (Object Path -> Presigned URL)
        List<String> imageUrls = objectPaths.stream()
                .map(path -> {
                    if (path == null || path.isEmpty()) {
                        return null; // objectPath가 null이면 Presigned URL 생성하지 않음
                    }
                    try {
                        return imageUtil.getPresignedDownloadUrl(path);
                    } catch (Exception e) {
                        throw new RuntimeException("Presigned URL 생성 중 오류 발생: " + e.getMessage(), e);
                    }
                })
                .filter(Objects::nonNull) // Presigned URL이 null이면 제거
                .collect(Collectors.toList());

        List<ReviewDetailResponseDto.CommentDto> comments = review.getReviewComments().stream()
                .map(comment -> ReviewDetailResponseDto.CommentDto.builder()
                        .commentId(comment.getId())
                        .writerId(comment.getWriter().getId())
                        .writerName(comment.getWriter().getName())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                        .build())
                .collect(Collectors.toList());

        try {
            return ReviewDetailResponseDto.builder()
                    .reviewId(review.getId())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .isPublic(review.getIsPublic())
                    .attended(true) // 참석 여부 확인 로직 추가 가능
                    .createdAt(review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                    .images(imageUrls) // 모든 Presigned URL 리스트 반환
                    .recruit(recruit != null ? ReviewDetailResponseDto.RecruitDto.builder()
                            .recruitId(recruit.getId())
                            .activityDate(recruit.getActivityDate().toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDate())
                            .activityStart(recruit.getActivityStart().doubleValue())
                            .activityEnd(recruit.getActivityEnd().doubleValue())
                            .status(recruit.getStatus())
                            .build() : null)
                    .category(category != null ? ReviewDetailResponseDto.CategoryDto.builder()
                            .categoryId(category.getId())
                            .name(category.getName())
                            .build() : null)
                    .writer(ReviewDetailResponseDto.WriterDto.builder()
                            .writerId(writer.getId())
                            .writerName(writer.getName())
                            .writerEmail(writer.getEmail())
                            .writerProfileImage(
                                    writer.getProfileImage() != null ? imageUtil.getPresignedDownloadUrl(writer.getProfileImage()) : null
                            ) // 작성자 프로필 이미지도 Presigned URL 변환
                            .build())
                    .template(template != null ? ReviewDetailResponseDto.TemplateDto.builder()
                            .templateId(template.getId())
                            .title(template.getTitle())
                            .activityLocation(template.getActivityLocation())
                            .status(template.getStatus())
                            .group(group != null ? ReviewDetailResponseDto.GroupDto.builder()
                                    .groupId(group.getId())
                                    .groupName(group.getGroupName())
                                    .build() : null)
                            .build() : null)
                    .organization(organization != null ? ReviewDetailResponseDto.OrganizationDto.builder()
                            .orgId(organization.getId())
                            .orgName(organization.getOrgName())
                            .build() : null)
                    .parentReviewId(review.getParentReview() != null ? review.getParentReview().getId() : null)
                    .comments(comments)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruitReviewResponseDto> recruitReview(Integer recruitId) {
        List<Review> reviews = reviewRepository.findByRecruitId(recruitId);

        return reviews.stream()
                .map(review -> RecruitReviewResponseDto.builder()
                        .review(RecruitReviewResponseDto.ReviewDto.builder()
                                .reviewId(review.getId())
                                .recruitId(review.getRecruit().getId())
                                .title(review.getTitle())
                                .content(review.getContent())
                                .isDeleted(review.getIsDeleted())
                                .updatedAt(review.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                                .createdAt(review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())

                                .build())
                        .writer(RecruitReviewResponseDto.WriterDto.builder()
                                .name(review.getWriter() != null ? review.getWriter().getName() : "Unknown")
                                .build())
                        .group(review.getRecruit().getTemplate() != null && review.getRecruit().getTemplate().getGroup() != null ?
                                RecruitReviewResponseDto.GroupDto.builder()
                                        .groupId(review.getRecruit().getTemplate().getGroup().getId())
                                        .groupName(review.getRecruit().getTemplate().getGroup().getGroupName())
                                        .build()
                                : null)
                        .organization(RecruitReviewResponseDto.OrganizationDto.builder()
                                .orgId(review.getOrg().getId())
                                .orgName(review.getOrg().getOrgName())
                                .build())
                        .images(
                                reviewImageRepository.findThumbnailImageByReviewId(review.getId())
                                        .orElse(null)
                        )
                        .build())
                .collect(Collectors.toList());
    }
}
