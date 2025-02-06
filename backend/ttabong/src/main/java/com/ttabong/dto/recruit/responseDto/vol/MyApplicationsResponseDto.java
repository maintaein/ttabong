package com.ttabong.dto.recruit.responseDto.vol;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MyApplicationsResponseDto {

    private int applicationId;
    private String status;
    private boolean evaluationDone;
    private LocalDateTime createdAt;
    private Template template;
    private Group group;
    private Recruit recruit;

    @Getter
    @Setter
    public static class Template {
        private int templateId;
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
    public static class Recruit {
        private int recruitId;
        private LocalDateTime deadline;
        private LocalDate activityDate;
        private double activityStart;
        private double activityEnd;
        private int maxVolunteer;
        private int participateVolCount;
        private String status;
        private LocalDateTime createdAt;
    }
}
