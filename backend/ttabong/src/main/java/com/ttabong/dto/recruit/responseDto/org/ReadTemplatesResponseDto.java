package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadTemplatesResponseDto {
    private List<GroupDto> groups;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupDto {
        private Integer groupId;
        private String groupName;
        private List<TemplateDto> templates;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TemplateDto {
        private Integer templateId;
        private Integer orgId;
        private Integer categoryId;
        private String title;
        private String activityLocation;
        private String status;
        private List<String> images;
        private String contactName;
        private String contactPhone;
        private String description;
        private LocalDateTime createdAt;
    }
}
