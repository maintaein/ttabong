package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadRecruitResponseDto {
    private Group group;
    private Template template;
    private Recruit recruit;
    private Organization organization;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Group {
        private int groupId;
        private String groupName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
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
    @NoArgsConstructor
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
    @NoArgsConstructor
    public static class Organization {
        private int orgId;
        private String name;
    }
}
