package com.ttabong.dto.sns;

import lombok.*;

/*
 * 7. 후기 _ 생성
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateResDto {
    private String message;
    private Long reviewId;
    private Long writerId;
}
