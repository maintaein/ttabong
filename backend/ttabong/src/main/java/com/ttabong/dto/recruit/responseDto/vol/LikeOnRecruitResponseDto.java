package com.ttabong.dto.recruit.responseDto.vol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeOnRecruitResponseDto {
    Integer relationId;
    Boolean isLike;
}
