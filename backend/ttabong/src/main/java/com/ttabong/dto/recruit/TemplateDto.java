package com.ttabong.dto.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateDto {

    private Long templateId;
    private Long groupId;
    private Long orgId;
    private Long categoryId;
    private String title;
    private String activityLocation;
    private String status;
    private String imageId;
    private String contactName;
    private String contactPhone;
    private String description;
    private Boolean isDeleted;
    private LocalDateTime createdAt;

}
