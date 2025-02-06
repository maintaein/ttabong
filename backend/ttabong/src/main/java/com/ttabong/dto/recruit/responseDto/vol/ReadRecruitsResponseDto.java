package com.ttabong.dto.recruit.responseDto.vol;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class ReadRecruitsResponseDto {
    private List<TemplateDetail> templates;

    @Getter
    @Setter
    public static class TemplateDetail {
        private Template template;
        private Group group;
        private Organization organization;
    }

    @Getter
    @Setter
    public static class Template {
        private int templateId;
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

    @Getter
    @Setter
    public static class Group {
        private int groupId;
        private String groupName;
    }

    @Getter
    @Setter
    public static class Organization {
        private int orgId;
        private String orgName;
    }
}