package com.ttabong.dto.sns;

import lombok.*;

/*
 * 6. 후기 _ 삭제
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDeleteResDto {
    private String message;
    private Long reviewId;
    private String title;
    private String content;
}
