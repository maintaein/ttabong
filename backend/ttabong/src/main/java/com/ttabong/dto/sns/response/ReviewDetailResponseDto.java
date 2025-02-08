package com.ttabong.dto.sns.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public class ReviewDetailResponseDto {
    // 후기 정보
    private Integer reviewId;
    private String title;
    private String content;
    private Boolean isPublic;
    private Boolean attended;
    private LocalDateTime createdAt;
    private List<String> images;

    // 공고 정보
    private Integer recruitId;
    private LocalDate activityDate;
    private Double activityStart;
    private Double activityEnd;
    private String status;

    // 카테고리 정보
    private Integer categoryId;
    private String categoryName;

    // 작성자 정보
    private Integer writerId;
    private String writerName;
    private String writerEmail;
    private String writerProfileImage;

    // 템플릿 정보
    private Integer templateId;
    private String templateTitle;
    private String activityLocation;
    private String templateStatus;

    // 그룹 정보 (템플릿 내부)
    private Integer groupId;
    private String groupName;

    // 기관 정보
    private Integer orgId;
    private String orgName;

    // 부모 후기 ID (nullable)
    private Integer parentReviewId;

    // 댓글 정보
    private List<CommentDto> comments;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentDto {
        private Integer commentId;
        private Integer writerId;
        private String writerName;
        private String content;
        private LocalDateTime createdAt;
    }
}
