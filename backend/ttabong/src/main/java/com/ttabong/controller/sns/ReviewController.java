package com.ttabong.controller.sns;

import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.response.ReviewCreateResponseDto;
import com.ttabong.dto.sns.response.ReviewDeleteResponseDto;
import com.ttabong.service.sns.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("mine")
    public ResponseEntity<?> readMyReviews(){

        return ResponseEntity.ok().build();
    }

    @GetMapping("{reviewId}")
    public ResponseEntity<?> readReviewDetail(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("recruits/{recruitId}")
    public ResponseEntity<?> readRelatedReviews(@PathVariable String recruitId){

        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<?> readAllReviews(@RequestParam Integer cursor, @RequestParam Integer limit){

        return ResponseEntity.ok().build();
    }

    @PatchMapping("{reviewId}/visibility")
    public ResponseEntity<?> updateReviewVisiblity() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("")
    public ResponseEntity<?> startCreateReview() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("")
    public ResponseEntity<?> endCreateReview() {
        return ResponseEntity.ok().build();
    }


    //For BBB
    @PostMapping("{reviewId}")
    public ResponseEntity<?> startUpdateReview() {
        return ResponseEntity.ok().build();
    }

    //For BBB
    @PatchMapping("{reviewId}")
    public ResponseEntity<?> endUpdateReview() {
        return ResponseEntity.ok().build();
    }
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