package com.ttabong.dto.sns;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long reviewId;         // 후기 ID
    private Long parentReviewId;   // 자기참조 (부모 리뷰 ID)
    private Long groupId;          // 그룹 ID
    private Long recruitId;        // 공고 ID
    private Long orgId;            // 작성하는 기관 ID
    private Long writerId;         // 작성자 ID
    private String title;          // 후기 제목
    private String content;        // 후기 내용
    private Boolean isDeleted;     // 삭제 여부
    private Boolean isPublic;      // 공개 여부
    private Long thumbnailImg;     // 썸네일 이미지 ID
    private Integer imgCount;      // 이미지 개수
    private LocalDateTime updatedAt; // 수정 시각
    private LocalDateTime createdAt; // 생성 일시
}
