package com.ttabong.dto.sns;

import lombok.*;

/*
 * 8. 후기 _ 수정
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateResDto {
    private String message;
    private Long reviewId;
    private Long writerId;
}
