package com.ttabong.dto.recruit.responseDto.vol;

public class ApplyRecruitResponseDto {
    String message;
    Application application;

    private class Application {
        Integer applicationId;
        String status;
    }
}
