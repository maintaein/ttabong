package com.ttabong.dto.recruit.responseDto.vol;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    }

    @Getter
    @Setter
    public static class Group {
        private Integer groupId;
        private String groupName;
    }

    @Getter
    @Setter
    public static class Organization {
        private Integer orgId;
        private String orgName;
    }
}
