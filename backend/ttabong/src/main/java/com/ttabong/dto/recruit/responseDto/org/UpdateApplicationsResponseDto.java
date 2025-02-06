package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateApplicationsResponseDto {
    private String message;
    private Application application;

    @Getter
    @Setter
    public static class Application {
        private int applicationId;
        private int recruitId;
        private String status;
        private LocalDateTime createdAt;
    }
}