package com.ttabong.dto.recruit.responseDto.vol;

import com.ttabong.entity.recruit.VolunteerReaction;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyLikesRecruitsResponseDto {
    private Integer templateId;
    private String thumbnailImg;
    private String activityLocation;
    private String title;
    private RecruitDto recruit;
    private GroupDto group;

    public static MyLikesRecruitsResponseDto from(VolunteerReaction reaction) {
        return MyLikesRecruitsResponseDto.builder()
                .templateId(reaction.getRecruit().getTemplate().getId())
                .thumbnailImg(reaction.getRecruit().getTemplate().getThumbnailImage() != null ?
                        reaction.getRecruit().getTemplate().getThumbnailImage().getImageUrl() : null)
                .activityLocation(reaction.getRecruit().getTemplate().getActivityLocation())
                .title(reaction.getRecruit().getTemplate().getTitle())
                .recruit(RecruitDto.from(reaction.getRecruit()))
                .group(GroupDto.from(reaction.getRecruit().getTemplate().getGroup()))
                .build();
    }
}
