package com.ttabong.controller.sns;

import com.ttabong.config.LoggerConfig;
import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.request.ReviewEditRequestDto;
import com.ttabong.dto.sns.request.ReviewVisibilitySettingRequestDto;
import com.ttabong.dto.sns.response.*;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.service.sns.ReviewService;
import com.ttabong.util.service.CacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController implements LoggerConfig {
    private final ReviewService reviewService;
    private final CacheService cacheService;

    // 1. 내가 쓴 후기 조회
    @GetMapping("/mine")
    public ResponseEntity<List<MyAllReviewPreviewResponseDto>> readMyAllReviews(
            @AuthenticationPrincipal AuthDto userPrincipal) { // 현재 로그인한 사용자 정보 가져오기
        logger().info("1. 후기 조회\"mine\"");
        List<MyAllReviewPreviewResponseDto> response = reviewService.readMyAllReviews(userPrincipal);

        return ResponseEntity.ok(response);

    }

    // 2. 해당 후기 상세 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailResponseDto> detailReview(
            @PathVariable(name = "reviewId") Integer reviewId) throws Exception {
        logger().info("2. 해당 후기 상세 조회\"/{reviewId}\"");
        ReviewDetailResponseDto response = reviewService.detailReview(reviewId);

        return ResponseEntity.ok(response);
    }

    // 3. 공고 관련 후기들 조회
    @GetMapping("/recruits/{recruitId}")
    public ResponseEntity<List<RecruitReviewResponseDto>> recruitReview(@PathVariable(name = "recruitId") Integer recruitId) {
        logger().info("3. 공고 관련 후기들 조회\"/recruits/{recruitId}\"");
        List<RecruitReviewResponseDto> response = reviewService.recruitReview(recruitId);

        return ResponseEntity.ok(response);

    }

    // 4. 후기 _ 전체 조회 (봉사자+기관) (미리보기)_피드부분
    @GetMapping
    public ResponseEntity<List<AllReviewPreviewResponseDto>> readAllReviews(
            @RequestParam(required = false, name = "reviewId") Integer cursor,
            @RequestParam(defaultValue = "10", name = "limit") Integer limit) {
        logger().info("4. 후기 _ 전체 조회\"/\"");
        List<AllReviewPreviewResponseDto> response = reviewService.readAllReviews(cursor, limit);

        return ResponseEntity.ok(response);

    }

    // 5. 후기 공개 여부 설정
    @PatchMapping("/{reviewId}/visibility")
    public ResponseEntity<ReviewVisibilitySettingResponseDto> updateVisibility(
            @PathVariable(name = "reviewId") Integer reviewId,
            @RequestBody ReviewVisibilitySettingRequestDto requestDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger().info("5. 후기 공개 여부 설정\"/{reviewId}/visibility\"");
        ReviewVisibilitySettingResponseDto response = reviewService.updateVisibility(reviewId, requestDto, authDto);

        return ResponseEntity.ok(response);

    }

    // TODO: 이미 삭제된거면 삭제된거라고 말하기
    @PatchMapping("/{reviewId}/delete")
    public ResponseEntity<ReviewDeleteResponseDto> deleteReview(
            @PathVariable(name = "reviewId") Integer reviewId,
            @AuthenticationPrincipal AuthDto authDto) {
        logger().info("6. 삭제하기 \"/{reviewId}/delete\"");
        ReviewDeleteResponseDto response = reviewService.deleteReview(reviewId, authDto);

        return ResponseEntity.ok(response);
    }


    // 7. 후기 작성 시작
    // minio Presigned URL 발급 API /api/reviews/{reviewId}/write
    @GetMapping("/write")
    public ResponseEntity<CreateReviewPresignedUrlResponseDto> generatePresignedUrls(@AuthenticationPrincipal AuthDto authDto) {
        logger().info("7. 후기 작성 시작 \"/write\"");
        if (authDto == null || authDto.getUserId() == null) {
            throw new SecurityException("로그인된 유저만 후기를 생성할 수 있습니다.");
        }

        List<String> presignedUrls = cacheService.generatePresignedUrlsForTemplate();

        CreateReviewPresignedUrlResponseDto response = CreateReviewPresignedUrlResponseDto.builder()
                .imageUrls(presignedUrls)
                .build();

        return ResponseEntity.ok().body(response);

    }

    // 8. 후기 작성 완료 /api/reviews/{reviewId}/write
    @PostMapping("/write")
    public ResponseEntity<ReviewCreateResponseDto> createReview(
            @AuthenticationPrincipal AuthDto authDto,
            @RequestBody @Valid ReviewCreateRequestDto requestDto) {
        logger().info("8. 후기 작성 완료 \"/write\"");
        ReviewCreateResponseDto response = reviewService.createReview(authDto, requestDto);

        return ResponseEntity.ok(response);

    }

    // 9. 후기수정 시작 _ (presigneed)요청
    @GetMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewEditStartResponseDto> startReviewEdit(
            @PathVariable(name = "reviewId") Integer reviewId,
            @AuthenticationPrincipal AuthDto authDto) {
        logger().info("9. 후기 작성 완료 \"/{reviewId}/edit\"");
        ReviewEditStartResponseDto response = reviewService.startReviewEdit(reviewId, authDto);

        return ResponseEntity.ok(response);

    }

    // 10. 후기 수정 완료
    @PatchMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewEditResponseDto> updateReview(
            @PathVariable(name = "reviewId") Integer reviewId,
            @RequestBody ReviewEditRequestDto requestDto,
            @AuthenticationPrincipal AuthDto authDto) {
        logger().info("10. 후기 작성 완료 \"/{reviewId}/edit\"");
        ReviewEditResponseDto response = reviewService.updateReview(reviewId, requestDto, authDto);

        return ResponseEntity.ok(response);
    }
}
