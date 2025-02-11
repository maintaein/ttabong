package com.ttabong.dto.recruit.responseDto.org;

import com.ttabong.dto.GlobalBaseDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateApplicationsResponseDto implements GlobalBaseDto {
    private String message;
    private Application application;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Application {
        private Integer applicationId;
        private Integer recruitId;
        private String status;
        private LocalDateTime createdAt;
    }
}
