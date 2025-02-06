package com.ttabong.dto.sns;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/*
 * 2. 후기 _ 후기 상세 조회 (개별 조회)
 * */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetailResDto {
    // 후기 정보
    private Long reviewId;
    private String title;
    private String content;
    private Boolean isPublic;
    private Boolean attended;
    private LocalDateTime createdAt;
    private List<String> images;

    // 공고 정보
    private Long recruitId;
    private LocalDate activityDate;
    private Double activityStart;
    private Double activityEnd;
    private String status;

    // 카테고리 정보
    private Long categoryId;
    private String categoryName;

    // 작성자 정보
    private Long writerId;
    private String writerName;
    private String writerEmail;
    private String writerProfileImage;

    // 템플릿 정보
    private Long templateId;
    private String templateTitle;
    private String activityLocation;
    private String templateStatus;

    // 그룹 정보 (템플릿 내부)
    private Long groupId;
    private String groupName;

    // 기관 정보
    private Long orgId;
    private String orgName;

    // 부모 후기 ID (nullable)
    private Long parentReviewId;

    // 댓글 정보
    private List<CommentDto> comments;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentDto {
        private Long commentId;
        private Long writerId;
        private String writerName;
        private String content;
        private LocalDateTime createdAt;
    }
}
