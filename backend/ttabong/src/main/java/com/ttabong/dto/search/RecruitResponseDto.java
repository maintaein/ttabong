package com.ttabong.dto.search;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class RecruitResponseDto {
    private List<TemplateDto> templates;
    private Integer nextCursor; // 다음 페이지 조회를 위한 cursor 값

    @Getter
    @Builder
    public static class TemplateDto {
        private Integer templateId;
        private Integer categoryId;
        private String title;
        private String activityLocation;
        private String status;
        private String imageId;
        private String contactName;
        private String contactPhone;
        private String description;
        private LocalDateTime createdAt;

        private GroupDto group;
        private OrganizationDto organization;
    }

    @Getter
    @Builder
    public static class GroupDto {
        private Integer groupId;
        private String groupName;
    }

    @Getter
    @Builder
    public static class OrganizationDto {
        private Integer orgId;
        private String orgName;
    }
}
