package com.ttabong.dto.recruit.responseDto.org;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class ReadTemplatesResponseDto {
    private List<GroupDto> groups;
    @Getter
    @Setter
    public class GroupDto {
        private int groupId;
        private String groupName;
        private List<TemplateDto> templates;
    }
    @Getter
    @Setter
    public class TemplateDto {
        private int templateId;
        private int orgId;
        private int categoryId;
        private String title;
        private String activityLocation;
        private String status;
        private String imageId;
        private String contactName;
        private String contactPhone;
        private String description;
        private LocalDateTime createdAt;
    }
}
