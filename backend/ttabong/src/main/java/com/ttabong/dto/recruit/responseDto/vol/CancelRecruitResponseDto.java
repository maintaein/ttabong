package com.ttabong.dto.recruit.responseDto.vol;

import lombok.Getter;
import lombok.Setter;

public class CancelRecruitResponseDto {
    String message;
    Application application;
    @Getter
    @Setter
    private class Application {
        Integer applicationId;
        Boolean isDeleted;
    }
}
