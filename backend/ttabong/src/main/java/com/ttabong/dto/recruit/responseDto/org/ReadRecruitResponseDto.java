package com.ttabong.dto.recruit.responseDto.org;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReadRecruitResponseDto {
    private Group group;
    private Template template;
    private Recruit recruit;
    private Organization organization;

    @Getter
    @Setter
    public static class Group {
        private int groupId;
        private String groupName;
    }

    @Getter
    @Setter
    public static class Template {
        private int templateId;
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

    @Getter
    @Setter
    public static class Recruit {
        private int recruitId;
        private LocalDateTime deadline;
        private LocalDate activityDate;
        private int activityStart; // 1000, 1400 같은 4자리 숫자로 표현
        private int activityEnd;
        private int maxVolunteer;
        private int participateVolCount;
        private String status;
        private LocalDateTime updatedAt;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    public static class Organization {
        private int orgId;
        private String name;
    }
}
