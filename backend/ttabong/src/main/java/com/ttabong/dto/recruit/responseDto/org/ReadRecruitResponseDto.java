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
        private Integer groupId;
        private String groupName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
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
    @NoArgsConstructor
    public static class Recruit {
        private Integer recruitId;
        private LocalDateTime deadline;
        private LocalDate activityDate;
        private Integer activityStart; // 1000, 1400 같은 4자리 숫자로 표현
        private Integer activityEnd;
        private Integer maxVolunteer;
        private Integer participateVolCount;
        private String status;
        private LocalDateTime updatedAt;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Organization {
        private Integer orgId;
        private String name;
    }
}
