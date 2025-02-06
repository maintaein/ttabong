package com.ttabong.dto.recruit.responseDto.vol;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class MyLikesRecruitsResponseDto {
    private List<LikedTemplate> likedTemplates;

    @Getter
    @Setter
    public static class LikedTemplate {
        private int templateId;
        private String thumbnailImg;
        private String activityLocation;
        private String title;
        private Recruit recruit;
        private Group group;
    }

    @Getter
    @Setter
    public static class Recruit {
        private LocalDateTime deadline;
    }

    @Getter
    @Setter
    public static class Group {
        private int groupId;
        private String groupName;
    }
}