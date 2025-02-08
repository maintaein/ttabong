package com.ttabong.dto.sns.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/*
 * 6. 후기 _ 삭제
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDeleteResponseDto {
    private String message;
    private Integer reviewId;
    private String title;
    private String content;
}
