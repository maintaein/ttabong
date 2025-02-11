package com.ttabong.dto.recruit.requestDto.org;

import com.ttabong.dto.GlobalBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluateApplicationsRequestDto implements GlobalBaseDto {
    private Integer volunteerId;
    private String recommendationStatus;
}
