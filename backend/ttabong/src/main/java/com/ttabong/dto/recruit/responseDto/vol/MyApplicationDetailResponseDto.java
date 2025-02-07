package com.ttabong.dto.recruit.responseDto.vol;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyApplicationDetailResponseDto {

    private Group group;
    private Template template;
    private Recruit recruit;
    private Organization organization;
    private Application application;

    @Getter
    @Setter
    public static class Group {
        private Integer groupId;
        private String groupName;
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
    public static class Recruit {
        private int recruitId;
        private LocalDateTime deadline;
        private LocalDate activityDate;
        private Integer activityStart;
        private Integer activityEnd;
        private Integer maxVolunteer;
        private Integer participateVolCount;
        private String status;
        private LocalDateTime updatedAt;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    public static class Organization {
        private Integer orgId;
        private String name;
    }

    @Getter
    @Setter
    public static class Application {
        private Integer applicationId;
        private String name;
        private String status;
    }
}