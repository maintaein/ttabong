package com.ttabong.dto.recruit.responseDto.vol;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadRecruitDetailResponseDto {
    private Template template;
    private Group group;
    private Category category;
    private Organization organization;
    private List<Recruit> recruits;

    @Getter
    @Setter
    public static class Template {
        private int templateId;
        private int categoryId;
        private String title;
        private String activityLocation;
        private String status;
        private List<String> images;
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
    public static class Category {
        private int categoryId;
        private String name;
    }

    @Getter
    @Setter
    public static class Organization {
        private int orgId;
        private String name;
    }

    @Getter
    @Setter
    public static class Recruit {
        private int recruitId;
        private LocalDateTime deadline;
        private LocalDate activityDate;
        private int activityStart;
        private int activityEnd;
        private int maxVolunteer;
        private int participateVolCount;
        private String status;
        private LocalDateTime updatedAt;
        private LocalDateTime createdAt;
    }
}