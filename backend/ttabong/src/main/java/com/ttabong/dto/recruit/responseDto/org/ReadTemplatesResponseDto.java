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
    public class GroupDto {
        private int groupId;
        private String groupName;
        private List<TemplateDto> templates;
    }

    @Getter
    @Setter
    @NoArgsConstructor
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
