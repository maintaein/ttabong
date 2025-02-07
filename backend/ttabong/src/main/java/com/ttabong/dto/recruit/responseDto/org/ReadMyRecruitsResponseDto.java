package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadMyRecruitsResponseDto {
    private List<RecruitDetail> recruits;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RecruitDetail {
        private Group group;
        private Template template;
        private Recruit recruit;
    }

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
        private String title;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Recruit {
        private Integer recruitId;
        private String status;
        private Integer maxVolunteer;
        private Integer participateVolCount;
        private LocalDate activityDate;
        private double activityStart;
        private double activityEnd;
        private LocalDateTime deadline;
        private LocalDateTime createdAt;
    }
}
