package com.ttabong.dto.sns.request;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/*
 * 7. 후기 _ 생성
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequestDto {
    @NotNull
    private Long recruitId;  // 공고 ID

    @NotNull
    private Long orgId;  // 기관 ID

    @NotNull
    private Long writerId;  // 작성자 ID

    @NotBlank
    private String title;  // 후기 제목

    @NotBlank
    private String content;  // 후기 내용

    @NotNull
    private Boolean isPublic;  // 공개 여부

    private List<String> images;  // 이미지 URL 리스트

    @NotNull
    private Integer imageCount;  // 이미지 개수
}
