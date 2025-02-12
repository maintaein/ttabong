package com.ttabong.dto.recruit.responseDto.vol;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyLikesRecruitsResponseDto {
    private List<LikedTemplate> likedTemplates;

    @Getter
    @Setter
    @Builder
    public static class LikedTemplate {
        private Integer templateId;
        private String thumbnailImg;
        private String activityLocation;
        private String title;
        private Recruit recruit;
        private Group group;
    }

    @Getter
    @Setter
    @Builder
    public static class Recruit {
        private LocalDateTime deadline;
    }

    @Getter
    @Setter
    @Builder
    public static class Group {
        private Integer groupId;
        private String groupName;
    }
}
