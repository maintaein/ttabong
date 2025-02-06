package com.ttabong.dto.sns.response;

import lombok.*;

/*
 * 7. 후기 _ 생성
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateResponseDto {
    private String message;
    private Long reviewId;
    private Long writerId;
}
