package com.ttabong.controller.sns;

import com.ttabong.dto.recruit.responseDto.org.CreateTemplateResponseDto;
import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.request.ReviewEditRequestDto;
import com.ttabong.dto.sns.response.ReviewCreateResponseDto;
import com.ttabong.dto.sns.response.ReviewDeleteResponseDto;
import com.ttabong.dto.sns.response.ReviewEditResponseDto;
import com.ttabong.dto.sns.response.ReviewEditStartResponseDto;
import com.ttabong.service.sns.ReviewService;
import com.ttabong.util.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final CacheService cacheService;

//    //For BBB
//    @PostMapping("{reviewId}")
//    public ResponseEntity<?> startUpdateReview() {
//        return ResponseEntity.ok().build();
//    }
//
//    //For BBB
//    @PatchMapping("{reviewId}")
//    public ResponseEntity<?> endUpdateReview() {
//        return ResponseEntity.ok().build();
//    }

    // minio Presigned URL 발급 API
    @GetMapping
    public ResponseEntity<CreateTemplateResponseDto> generatePresignedUrls() throws Exception {
        List<String> presignedUrls = cacheService.generatePresignedUrlsForTemplate();

        CreateTemplateResponseDto response =  CreateTemplateResponseDto.builder()
                .message("Presigned URL 생성 완료")
                .images(presignedUrls)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("mine")
    public ResponseEntity<?> readMyReviews() {

        return ResponseEntity.ok().build();
    }

    @GetMapping("{reviewId}")
    public ResponseEntity<?> readReviewDetail() {
        return ResponseEntity.ok().build();
    }

    /* 후기 생성 */
    @PostMapping
    public ResponseEntity<ReviewCreateResponseDto> createReview(@RequestBody ReviewCreateRequestDto requestDto) {
        ReviewCreateResponseDto response = reviewService.createReview(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //후기수정(presigneed )요청
    @GetMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewEditStartResponseDto> startReviewEdit(@PathVariable Integer reviewId) {
        ReviewEditStartResponseDto response = reviewService.startReviewEdit(reviewId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewEditResponseDto> updateReview(
            @PathVariable Integer reviewId,
            @RequestBody ReviewEditRequestDto requestDto) {

        ReviewEditResponseDto response = reviewService.updateReview(reviewId, requestDto);
        return ResponseEntity.ok(response);
    }

    /* 후기 삭제 */
    @PatchMapping("/{reviewId}/delete")
    public ResponseEntity<ReviewDeleteResponseDto> deleteReview(@PathVariable Integer reviewId) {
        ReviewDeleteResponseDto response = reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("recruits/{recruitId}")
    public ResponseEntity<?> readRelatedReviews(@PathVariable String recruitId) {

        return ResponseEntity.ok().build();
    }

//    @GetMapping()
//    public ResponseEntity<?> readAllReviews(@RequestParam Integer cursor, @RequestParam Integer limit) {
//
//        return ResponseEntity.ok().build();
//    }

    @PatchMapping("{reviewId}/visibility")
    public ResponseEntity<?> updateReviewVisiblity() {
        return ResponseEntity.ok().build();
    }

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