package com.ttabong.dto.recruit.responseDto.vol;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyApplicationsResponseDto {

    private Integer applicationId;
    private String status;
    private boolean evaluationDone;
    private LocalDateTime createdAt;
    private Template template;
    private Group group;
    private Recruit recruit;

    @Getter
    @Setter
    public static class Template {
        private Integer templateId;
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
    public static class Recruit {
        private Integer recruitId;
        private LocalDateTime deadline;
        private LocalDate activityDate;
        private double activityStart;
        private double activityEnd;
        private Integer maxVolunteer;
        private Integer participateVolCount;
        private String status;
        private LocalDateTime createdAt;
    }
}
