package com.ttabong.dto.recruit.responseDto.vol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyRecruitResponseDto {
    String message;
    Application application;

    private class Application {
        Integer applicationId;
        String status;
    }
}
