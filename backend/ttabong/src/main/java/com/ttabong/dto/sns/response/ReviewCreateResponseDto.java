package com.ttabong.dto.sns.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * 7. 후기 _ 생성
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateResponseDto {
    private String message;
    private Integer reviewId;
    private Integer writerId;
    private List<String> uploadedImages;
}
