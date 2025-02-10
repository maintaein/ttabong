package com.ttabong.controller.sns;

import com.ttabong.dto.recruit.responseDto.org.CreateTemplateResponseDto;
import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.request.ReviewEditRequestDto;
import com.ttabong.dto.sns.request.ReviewVisibilitySettingRequestDto;
import com.ttabong.dto.sns.response.*;
import com.ttabong.service.sns.ReviewService;
import com.ttabong.util.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final CacheService cacheService;

    // minio Presigned URL 발급 API
    @GetMapping("/write")
    public ResponseEntity<CreateTemplateResponseDto> generatePresignedUrls() throws Exception {
        List<String> presignedUrls = cacheService.generatePresignedUrlsForTemplate();

        CreateTemplateResponseDto response =  CreateTemplateResponseDto.builder()
                .message("Presigned URL 생성 완료")
                .images(presignedUrls)
                .build();

        return ResponseEntity.ok().body(response);
    }

    /* 후기 생성 */
    @PostMapping
    public ResponseEntity<ReviewCreateResponseDto> createReview(@RequestBody ReviewCreateRequestDto requestDto) {
        ReviewCreateResponseDto response = reviewService.createReview(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //후기수정(presigneed )요청
    @GetMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewEditStartResponseDto> startReviewEdit(@PathVariable(name = "reviewId") Integer reviewId) {
        ReviewEditStartResponseDto response = reviewService.startReviewEdit(reviewId);
        return ResponseEntity.ok(response);
    }

    // 후기 수정
    @PatchMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewEditResponseDto> updateReview(
            @PathVariable(name = "reviewId") Integer reviewId,
            @RequestBody ReviewEditRequestDto requestDto) {

        ReviewEditResponseDto response = reviewService.updateReview(reviewId, requestDto);
        return ResponseEntity.ok(response);
    }

    /* 후기 삭제 */
    @PatchMapping("/{reviewId}/delete")
    public ResponseEntity<ReviewDeleteResponseDto> deleteReview(@PathVariable(name = "reviewId") Integer reviewId) {
        ReviewDeleteResponseDto response = reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(response);
    }

    // 5. 후기 공개 여부 설정
    @PatchMapping("/{reviewId}/visibility")
    public ResponseEntity<ReviewVisibilitySettingResponseDto> updateVisibility(
            @PathVariable(name = "reviewId") Integer reviewId,
            @RequestBody ReviewVisibilitySettingRequestDto requestDto) {
        ReviewVisibilitySettingResponseDto response = reviewService.updateVisibility(reviewId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 4. 후기 _ 전체 조회 (봉사자+기관) (미리보기)_피드부분
    // /api/reviews?cursor={reviewId}&limit={limit}
    @GetMapping
    public ResponseEntity<List<AllReviewPreviewResponseDto>> readAllReviews(
            @RequestParam(required = false, name = "reviewId") Integer cursor,
            @RequestParam(defaultValue = "10", name = "limit") Integer limit) {

        List<AllReviewPreviewResponseDto> response = reviewService.readAllReviews(cursor, limit);

        return ResponseEntity.ok(response);
    }

//    @GetMapping("recruits/{recruitId}")
//    public ResponseEntity<?> readRelatedReviews(@PathVariable String recruitId) {
//
//        return ResponseEntity.ok().build();
//    }

//    @GetMapping()
//    public ResponseEntity<?> readAllReviews(@RequestParam Integer cursor, @RequestParam Integer limit) {
//
//        return ResponseEntity.ok().build();
//    }

//    @PostMapping("")
//    public ResponseEntity<?> startCreateReview() {
//        return ResponseEntity.ok().build();
//    }
//
//    @PutMapping("")
//    public ResponseEntity<?> endCreateReview() {
//        return ResponseEntity.ok().build();
//    }



}
/*
후기 _ 자신이 쓴 후기 (미리보기) (모두 조회)	GET	/api/reviews/mine
후기 _ 후기 상세 조회 (개별 조회)	GET	/api/reviews/{리뷰id}
후기 _ 공고 관련 봉사자의 후기 조회	GET	/api/reviews/recruits/{recruitId}
후기 _ 전체 조회 (봉사자+기관) (미리보기)_피드부분	GET	/api/reviews?cursor={reviewId}&limit={limit}
후기 _ 공개 여부 설정	PATCH	/api/reviews/{reviewId}/visibility
후기 _ 삭제	PATCH	/api/reviews/{리뷰id}/delete
후기_작성시작	POST	/api/reviews
후기_작성완료	PUT	/api/reviews
후기_수정시작	POST	/api/reviews/{reviewId}
후기_수정완료	PATCH	/api/reviews/{reviewId}
 */