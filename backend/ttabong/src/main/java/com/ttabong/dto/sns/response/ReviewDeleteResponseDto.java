package com.ttabong.dto.sns.response;

import lombok.*;

/*
 * 6. 후기 _ 삭제
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDeleteResponseDto {
    private String message;
    private Long reviewId;
    private String title;
    private String content;
}
