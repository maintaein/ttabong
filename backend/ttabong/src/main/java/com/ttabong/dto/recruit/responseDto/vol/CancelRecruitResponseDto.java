package com.ttabong.dto.recruit.responseDto.vol;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
