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
import com.ttabong.entity.sns.ReviewComment;
import com.ttabong.entity.sns.ReviewImage;
import com.ttabong.entity.user.Organization;
import com.ttabong.entity.user.User;
import com.ttabong.exception.ConflictException;
import com.ttabong.exception.ForbiddenException;
import com.ttabong.exception.NotFoundException;
import com.ttabong.exception.UnauthorizedException;
import com.ttabong.repository.recruit.ApplicationRepository;
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
import java.util.*;
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
    private final ApplicationRepository applicationRepository;
    private final TemplateRepository templateRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final CacheUtil cacheUtil;
    private final ImageUtil imageUtil;
    private final MinioClient minioClient;

    public void checkToken(AuthDto authDto) {
        if (authDto == null || authDto.getUserId() == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
    }

    // TODO: 기관이 후기 생성시, 봉사자가 해당 공고에 관해 쓴 후기들에 대해서 parentid값 설정 필요
    @Override
    public ReviewCreateResponseDto createReview(AuthDto authDto, ReviewCreateRequestDto requestDto) {

        checkToken(authDto);

        final User writer = userRepository.findById(authDto.getUserId())
                .orElseThrow(() -> new NotFoundException("작성자 없음"));

        final Recruit recruit = recruitRepository.findById(requestDto.getRecruitId())
                .orElseThrow(() -> new NotFoundException("해당 공고 없음"));

        final Template template = recruit.getTemplate();
        final Integer groupId = template.getGroup().getId();

        boolean alreadyReviewed = reviewRepository.existsByWriterAndRecruit(writer.getId(), recruit.getId());
        if (alreadyReviewed) {
            throw new ConflictException("이미 해당 모집 공고에 대한 후기를 작성하였습니다.");
        }

        Organization organization;
        Review parentReview = null;

        if (organizationRepository.existsByUserId(authDto.getUserId())) {
            organization = organizationRepository.findByUserId(authDto.getUserId())
                    .orElseThrow(() -> new NotFoundException("기관 정보 없음"));

            if (!template.getOrg().getUser().getId().equals(authDto.getUserId())) {
                throw new ForbiddenException("해당 기관의 리뷰만 작성할 수 있습니다.");
            }

        } else {
            boolean isApplicant = applicationRepository.existsByVolunteerUserIdAndRecruitId(writer.getId(), recruit.getId());
            if (!isApplicant) {
                throw new ForbiddenException("해당 봉사 모집 공고에 참여한 사용자만 리뷰를 작성할 수 있습니다.");
            }

            organization = template.getOrg();

            parentReview = reviewRepository.findFirstByOrgWriterAndRecruit(recruit.getId())
                    .orElse(null);
        }

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

        List<ReviewImage> imageSlots = IntStream.range(0, requestDto.getImageCount())
                .mapToObj(i -> ReviewImage.builder()
                        .review(review)
                        .template(template)
                        .imageUrl(null)
                        .isThumbnail(i == 0)
                        .isDeleted(false)
                        .createdAt(Instant.now())
                        .build())
                .collect(Collectors.toList());

        for (int i = 0; i < imageSlots.size() - 1; i++) {
            imageSlots.get(i).setNextImage(imageSlots.get(i + 1));
        }

        reviewImageRepository.saveAll(imageSlots);
        uploadImagesToMinio(requestDto.getUploadedImages(), imageSlots);

        return ReviewCreateResponseDto.builder()
                .message("리뷰가 생성되었습니다.")
                .uploadedImages(requestDto.getUploadedImages())
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
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        Review updatedReview = review.toBuilder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .isPublic(requestDto.getIsPublic())
                .updatedAt(Instant.now())
                .build();
        reviewRepository.save(updatedReview);

        List<ReviewImage> existingImages = reviewImageRepository.findByReviewIdOrderByIdAsc(reviewId);

        if (existingImages.isEmpty()) {
            existingImages = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> ReviewImage.builder()
                            .review(review)
                            .imageUrl(null)  // 기본값은 null
                            .isThumbnail(i == 1) // 첫 번째 이미지만 썸네일
                            .isDeleted(false)
                            .createdAt(Instant.now())
                            .build())
                    .collect(Collectors.toList());

            reviewImageRepository.saveAll(existingImages);
        }

        List<String> presignedUrls = requestDto.getPresignedUrl();
        int newSize = Math.min(presignedUrls.size(), 10); // 최대 10개까지만 허용

        for (int i = 0; i < 10; i++) {
            ReviewImage imageSlot = existingImages.get(i);

            if (i < newSize) {  // 새로운 이미지로 업데이트
                String objectPath = cacheUtil.findObjectPath(presignedUrls.get(i));
                if (objectPath == null) {
                    throw new RuntimeException("유효하지 않은 presigned URL입니다.");
                }

                try {
                    if (imageSlot.getImageUrl() != null) {
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket("ttabong-bucket")
                                        .object(imageSlot.getImageUrl())
                                        .build()
                        );
                    }
                } catch (Exception e) {
                    throw new RuntimeException("MinIO에서 기존 이미지 삭제 실패", e);
                }

                imageSlot = imageSlot.toBuilder()
                        .imageUrl(objectPath)
                        .isThumbnail(i == 0) // 첫 번째 이미지만 썸네일 설정
                        .isDeleted(false)
                        .build();
            } else {  // 새로운 이미지가 없는 경우, 기존 슬롯 초기화
                imageSlot = imageSlot.toBuilder()
                        .imageUrl(null)
                        .isThumbnail(false)
                        .isDeleted(false)
                        .build();
            }
            reviewImageRepository.save(imageSlot);
        }

        List<String> objectPaths = reviewImageRepository.findByReviewIdOrderByIdAsc(reviewId)
                .stream()
                .map(ReviewImage::getImageUrl)
                .filter(Objects::nonNull)
                .toList();

        List<String> imageUrls = objectPaths.stream()
                .map(path -> {
                    try {
                        return imageUtil.getPresignedDownloadUrl(path);
                    } catch (Exception e) {
                        throw new RuntimeException("Presigned URL 생성 중 오류 발생: " + e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());


        return ReviewEditResponseDto.builder()
                .cacheId(requestDto.getCacheId())
                .title(updatedReview.getTitle())
                .content(updatedReview.getContent())
                .isPublic(updatedReview.getIsPublic())
                .imageCount(newSize)
                .images(imageUrls)
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
                .map(review -> {
                    String objectPath = reviewImageRepository
                            .findThumbnailImageByReviewId(review.getId(), PageRequest.of(0, 1))
                            .stream().findFirst().orElse(null);

                    String thumbnailUrl;
                    try {
                        thumbnailUrl = (objectPath != null) ? imageUtil.getPresignedDownloadUrl(objectPath) : null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    return AllReviewPreviewResponseDto.builder()
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
                            .images(thumbnailUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<MyAllReviewPreviewResponseDto> readMyAllReviews(AuthDto authDto) {

        checkToken(authDto);

        List<Review> reviews = reviewRepository.findDistinctByWriterId(authDto.getUserId(), PageRequest.of(0, 10));

        if (reviews.isEmpty()) {
            throw new NotFoundException("작성한 리뷰가 없습니다.");
        }

        return reviews.stream()
                .filter(review -> !review.getIsDeleted())
                .map(review -> {
                    String objectPath = reviewImageRepository
                            .findThumbnailImageByReviewId(review.getId(), PageRequest.of(0, 1))
                            .stream().findFirst().orElse(null);

                    String presignedUrl = null;
                    if (objectPath != null) {
                        try {
                            presignedUrl = imageUtil.getPresignedDownloadUrl(objectPath);
                        } catch (Exception e) {
                            presignedUrl = null;
                        }
                    }

                    Recruit recruit = review.getRecruit();
                    Template template = (recruit != null) ? recruit.getTemplate() : null;
                    TemplateGroup group = (template != null) ? template.getGroup() : null;
                    Organization org = review.getOrg();

                    return MyAllReviewPreviewResponseDto.builder()
                            .review(MyAllReviewPreviewResponseDto.ReviewDto.builder()
                                    .reviewId(review.getId())
                                    .recruitId((recruit != null) ? recruit.getId() : null)
                                    .title(review.getTitle())
                                    .content(review.getContent())
                                    .isDeleted(review.getIsDeleted())
                                    .updatedAt(review.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                                    .createdAt(review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                                    .build()
                            )
                            .group(MyAllReviewPreviewResponseDto.GroupDto.builder()
                                    .groupId((group != null) ? group.getId() : null)
                                    .groupName((group != null) ? group.getGroupName() : "N/A")
                                    .build()
                            )
                            .organization(MyAllReviewPreviewResponseDto.OrganizationDto.builder()
                                    .orgId((org != null) ? org.getId() : null)
                                    .orgName((org != null) ? org.getOrgName() : "N/A")
                                    .build()
                            )
                            .images(presignedUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public ReviewDetailResponseDto detailReview(Integer reviewId) {

        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new NotFoundException("해당 후기를 찾을 수 없습니다. reviewId: " + reviewId));

        User writer = review.getWriter();
        Recruit recruit = review.getRecruit();
        Template template = Optional.ofNullable(recruit).map(Recruit::getTemplate).orElse(null);
        TemplateGroup group = Optional.ofNullable(template).map(Template::getGroup).orElse(null);
        Category category = Optional.ofNullable(template).map(Template::getCategory).orElse(null);
        Organization organization = review.getOrg();

        List<String> objectPaths = reviewImageRepository.findAllImagesByReviewId(review.getId())
                .stream()
                .filter(Objects::nonNull)
                .toList();

        List<String> imageUrls = objectPaths.stream()
                .map(path -> {
                    if (path == null || path.isEmpty()) return null;
                    try {
                        return imageUtil.getPresignedDownloadUrl(path);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String profileImageUrl = null;
        if (writer.getProfileImage() != null) {
            try {
                profileImageUrl = imageUtil.getPresignedDownloadUrl(writer.getProfileImage());
            } catch (Exception e) {
                profileImageUrl = null;
            }
        }

        List<ReviewDetailResponseDto.CommentDto> comments = review.getReviewComments().stream()
                .sorted(Comparator.comparing(ReviewComment::getCreatedAt))
                .map(comment -> ReviewDetailResponseDto.CommentDto.builder()
                        .commentId(comment.getId())
                        .writerId(comment.getWriter().getId())
                        .writerName(comment.getWriter().getName())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                        .build())
                .collect(Collectors.toList());

        return ReviewDetailResponseDto.builder()
                .reviewId(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .isPublic(review.getIsPublic())
                .attended(true)
                .createdAt(review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .images(imageUrls)
                .recruit(Optional.ofNullable(recruit).map(r -> ReviewDetailResponseDto.RecruitDto.builder()
                        .recruitId(r.getId())
                        .activityDate(r.getActivityDate().toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDate())
                        .activityStart(r.getActivityStart().doubleValue())
                        .activityEnd(r.getActivityEnd().doubleValue())
                        .status(r.getStatus())
                        .build()).orElse(null))
                .category(Optional.ofNullable(category).map(c -> ReviewDetailResponseDto.CategoryDto.builder()
                        .categoryId(c.getId())
                        .name(c.getName())
                        .build()).orElse(null))
                .writer(ReviewDetailResponseDto.WriterDto.builder()
                        .writerId(writer.getId())
                        .writerName(writer.getName())
                        .writerEmail(writer.getEmail())
                        .writerProfileImage(profileImageUrl)
                        .build())
                .template(Optional.ofNullable(template).map(t -> ReviewDetailResponseDto.TemplateDto.builder()
                        .templateId(t.getId())
                        .title(t.getTitle())
                        .activityLocation(t.getActivityLocation())
                        .status(t.getStatus())
                        .group(Optional.ofNullable(group).map(g -> ReviewDetailResponseDto.GroupDto.builder()
                                .groupId(g.getId())
                                .groupName(g.getGroupName())
                                .build()).orElse(null))
                        .build()).orElse(null))
                .organization(Optional.ofNullable(organization).map(o -> ReviewDetailResponseDto.OrganizationDto.builder()
                        .orgId(o.getId())
                        .orgName(o.getOrgName())
                        .build()).orElse(null))
                .parentReviewId(Optional.ofNullable(review.getParentReview()).map(Review::getId).orElse(null))
                .comments(comments)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruitReviewResponseDto> recruitReview(Integer recruitId) {

        List<Review> reviews = reviewRepository.findByRecruitIdAndIsDeletedFalse(recruitId);

        if (reviews.isEmpty()) {
            throw new NotFoundException("해당 모집 공고에 대한 리뷰가 없습니다.");
        }

        return reviews.stream()
                .map(review -> {
                    String objectPath = reviewImageRepository
                            .findThumbnailImageByReviewId(review.getId(), PageRequest.of(0, 1))
                            .stream().findFirst().orElse(null);

                    String thumbnailUrl = null;
                    if (objectPath != null) {
                        try {
                            thumbnailUrl = imageUtil.getPresignedDownloadUrl(objectPath);
                        } catch (Exception e) {
                            thumbnailUrl = null;
                        }
                    }

                    Recruit recruit = review.getRecruit();
                    Template template = (recruit != null) ? recruit.getTemplate() : null;
                    TemplateGroup group = (template != null) ? template.getGroup() : null;
                    Organization org = review.getOrg();

                    return RecruitReviewResponseDto.builder()
                            .review(RecruitReviewResponseDto.ReviewDto.builder()
                                    .reviewId(review.getId())
                                    .recruitId((recruit != null) ? recruit.getId() : null)
                                    .title(review.getTitle())
                                    .content(review.getContent())
                                    .isDeleted(review.getIsDeleted())
                                    .updatedAt(review.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                                    .createdAt(review.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                                    .build()
                            )
                            .writer(RecruitReviewResponseDto.WriterDto.builder()
                                    .name((review.getWriter() != null) ? review.getWriter().getName() : "Unknown")
                                    .build()
                            )
                            .group((group != null) ? RecruitReviewResponseDto.GroupDto.builder()
                                    .groupId(group.getId())
                                    .groupName(group.getGroupName())
                                    .build() : null
                            )
                            .organization(RecruitReviewResponseDto.OrganizationDto.builder()
                                    .orgId((org != null) ? org.getId() : null)
                                    .orgName((org != null) ? org.getOrgName() : "N/A")
                                    .build()
                            )
                            .images(thumbnailUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

}
