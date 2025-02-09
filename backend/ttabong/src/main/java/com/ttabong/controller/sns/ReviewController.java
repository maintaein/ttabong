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
    @GetMapping
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
}
