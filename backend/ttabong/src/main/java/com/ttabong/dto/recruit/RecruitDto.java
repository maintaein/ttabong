package com.ttabong.dto.recruit;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitDto {
    private Long recruitId;
    private Long templateId;
    private LocalDateTime deadline;
    private LocalDate activityDate;
    private String activityTime;
    private Integer maxVolunteer;
    private Integer participateVolCount;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
