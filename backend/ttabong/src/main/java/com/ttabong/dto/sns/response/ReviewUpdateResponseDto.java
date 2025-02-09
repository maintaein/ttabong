package com.ttabong.dto.sns.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 8. 후기 _ 수정
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateResponseDto {
    private String message;
    private Integer reviewId;
    private Integer writerId;
}
