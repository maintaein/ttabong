package com.ttabong.dto.sns.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/*
 * 8. 후기 _ 수정
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateResponseDto {
    private String message;
    private Long reviewId;
    private Long writerId;
}
