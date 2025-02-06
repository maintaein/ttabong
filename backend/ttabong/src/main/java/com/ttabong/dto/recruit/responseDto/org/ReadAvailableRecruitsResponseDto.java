package com.ttabong.dto.recruit.responseDto.org;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class ReadAvailableRecruitsResponseDto {
        private List<TemplateDetail> templates;

        @Getter
        @Setter
        public static class TemplateDetail {
            private Template template;
            private Group group;
            private List<Recruit> recruits;
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
        public static class Group {
            private int groupId;
            private String groupName;
        }

        @Getter
        @Setter
        public static class Recruit {
            private int recruitId;
            private LocalDateTime deadline;
            private LocalDateTime activityDate;
            private int activityStart; // 시간은 4자리 숫자로 표현 (1000, 1400)
            private int activityEnd;
            private int maxVolunteer;
            private int participateVolCount;
            private String status;
            private LocalDateTime updatedAt;
            private LocalDateTime createdAt;
        }
    }