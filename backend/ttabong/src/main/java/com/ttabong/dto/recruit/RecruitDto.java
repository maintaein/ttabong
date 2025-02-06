package com.ttabong.dto.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitDto {

    private Long recruitId;
    private Long templateId;
    private LocalDateTime deadline;
    private LocalDate activityDate;
    private Double activityStart;
    private Double activityEnd;
    private Long maxVolunteer;
    private Long participateVolCount;
    private String status;
    private Long isDeleted;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
