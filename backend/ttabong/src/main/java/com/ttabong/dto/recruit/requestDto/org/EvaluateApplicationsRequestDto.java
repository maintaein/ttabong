package com.ttabong.dto.recruit.requestDto.org;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluateApplicationsRequestDto {
    private int volunteerId;
    private String recommendationStatus;
}
