package com.ttabong.dto.recruit.responseDto.vol;

import com.ttabong.entity.recruit.Template;

import java.time.Instant;
import java.util.List;

public class TemplateFullResponseDto {
    private Integer id;
    private Integer groupId;
    private Integer orgId;
    private Integer categoryId;
    private String title;
    private String activityLocation;
    private String status;
    private String contactName;
    private String contactPhone;
    private String description;
    private Boolean isDeleted;
    private Instant createdAt;
    private List<RecruitFullResponseDto> recruits;

    public TemplateFullResponseDto(Template template, List<RecruitFullResponseDto> recruits) {
        this.id = template.getId();
        this.groupId = template.getGroup() != null ? template.getGroup().getId() : null;
        this.orgId = template.getOrg() != null ? template.getOrg().getId() : null;
        this.categoryId = template.getCategory() != null ? template.getCategory().getId() : null;
        this.title = template.getTitle();
        this.activityLocation = template.getActivityLocation();
        this.status = template.getStatus();
        this.contactName = template.getContactName();
        this.contactPhone = template.getContactPhone();
        this.description = template.getDescription();
        this.isDeleted = template.getIsDeleted();
        this.createdAt = template.getCreatedAt();
        this.recruits = recruits;
    }

    // Getters, setters (또는 Lombok 어노테이션 사용)
}

