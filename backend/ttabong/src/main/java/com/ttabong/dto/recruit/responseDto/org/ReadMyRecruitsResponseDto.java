package com.ttabong.dto.recruit.responseDto.org;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReadMyRecruitsResponseDto {
    private List<RecruitDetail> recruits;

    @Getter
    @Setter
    public static class RecruitDetail {
        private Group group;
        private Template template;
        private Recruit recruit;
    }

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
        private String title;
    }

    @Getter
    @Setter
    public static class Recruit {
        private int recruitId;
        private String status;
        private int maxVolunteer;
        private int participateVolCount;
        private LocalDate activityDate;
        private double activityStart;
        private double activityEnd;
        private LocalDateTime deadline;
        private LocalDateTime createdAt;
    }
}
