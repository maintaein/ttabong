package com.ttabong.dto.sns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private Long parentReviewId;
    private Long groupId;
    private Long recruitId;
    private Long orgId;
    private Long writerId;
    private String title;
    private String content;
    private Boolean isDeleted;
    private Boolean isPublic;
    private Long thumbnailImg;
    private Integer imgCount;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
