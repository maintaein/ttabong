package com.ttabong.service.sns;

import com.ttabong.dto.sns.request.ReviewCreateRequestDto;
import com.ttabong.dto.sns.response.ReviewCreateResponseDto;
import com.ttabong.dto.sns.response.ReviewDeleteResponseDto;

public interface ReviewService {
    ReviewCreateResponseDto createReview(ReviewCreateRequestDto reviewCreateRequestDto);
    ReviewDeleteResponseDto deleteReview(Long reviewId);
}
