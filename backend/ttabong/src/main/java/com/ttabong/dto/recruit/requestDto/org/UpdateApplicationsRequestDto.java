package com.ttabong.dto.recruit.requestDto.org;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateApplicationsRequestDto {

    Integer recruitId;
    Integer volunteerId;
    Integer applicationId;
    Boolean accept;
}