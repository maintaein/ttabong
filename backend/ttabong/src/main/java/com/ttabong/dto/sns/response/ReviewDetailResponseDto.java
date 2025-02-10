package com.ttabong.dto.sns.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private RecruitDto recruit;

    // 카테고리 정보
    private CategoryDto category;

    // 작성자 정보
    private WriterDto writer;

    // 템플릿 정보 (그룹 포함)
    private TemplateDto template;

    // 기관 정보
    private OrganizationDto organization;

    // 부모 후기 ID (nullable)
    private Integer parentReviewId;

    // 댓글 정보
    private List<CommentDto> comments;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecruitDto {
        private Integer recruitId;
        private LocalDate activityDate;
        private Double activityStart;
        private Double activityEnd;
        private String status;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryDto {
        private Integer categoryId;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WriterDto {
        private Integer writerId;
        private String writerName;
        private String writerEmail;
        private String writerProfileImage;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TemplateDto {
        private Integer templateId;
        private String title;
        private String activityLocation;
        private String status;
        private GroupDto group;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupDto {
        private Integer groupId;
        private String groupName;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrganizationDto {
        private Integer orgId;
        private String orgName;
    }

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
