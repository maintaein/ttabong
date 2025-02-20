package com.ttabong.dto.recruit.responseDto.vol;

import com.ttabong.entity.recruit.Recruit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

public class RecruitFullResponseDto {
    private Integer id;
    private Integer templateId;
    private Instant deadline;
    private Date activityDate;
    private BigDecimal activityStart;
    private BigDecimal activityEnd;
    private Integer maxVolunteer;
    private Integer participateVolCount;
    private String status;
    private Boolean isDeleted;
    private Instant updatedAt;
    private Instant createdAt;

    public RecruitFullResponseDto(Recruit recruit) {
        this.id = recruit.getId();
        this.templateId = (recruit.getTemplate() != null) ? recruit.getTemplate().getId() : null;
        this.deadline = recruit.getDeadline();
        this.activityDate = recruit.getActivityDate();
        this.activityStart = recruit.getActivityStart();
        this.activityEnd = recruit.getActivityEnd();
        this.maxVolunteer = recruit.getMaxVolunteer();
        this.participateVolCount = recruit.getParticipateVolCount();
        this.status = recruit.getStatus();
        this.isDeleted = recruit.getIsDeleted();
        this.updatedAt = recruit.getUpdatedAt();
        this.createdAt = recruit.getCreatedAt();
    }

}
