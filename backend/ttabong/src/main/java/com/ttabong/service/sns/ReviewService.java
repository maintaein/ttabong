package com.ttabong.service.sns;

import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.request.ReviewEditRequestDto;
import com.ttabong.dto.sns.response.ReviewCreateResponseDto;
import com.ttabong.dto.sns.response.ReviewDeleteResponseDto;
import com.ttabong.dto.sns.response.ReviewEditResponseDto;
import com.ttabong.dto.sns.response.ReviewEditStartResponseDto;

public interface ReviewService {
    ReviewCreateResponseDto createReview(ReviewCreateRequestDto reviewCreateRequestDto);

    ReviewDeleteResponseDto deleteReview(Integer reviewId);

    ReviewEditStartResponseDto startReviewEdit(Integer reviewId);

    ReviewEditResponseDto updateReview(Integer reviewId, ReviewEditRequestDto requestDto);
}
