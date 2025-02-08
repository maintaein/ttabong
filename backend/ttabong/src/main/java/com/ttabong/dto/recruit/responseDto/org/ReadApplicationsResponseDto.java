package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadApplicationsResponseDto {
    private Integer recruitId;
    private List<ApplicationDetail> applications;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplicationDetail {
        private User user;
        private Volunteer volunteer;
        private Application application;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class User {
        private Integer userId;
        private String email;
        private String name;
        private String profileImage;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Volunteer {
        private Integer volunteerId;
        private Integer recommendedCount;
        private Integer totalVolunteerHours;
    }

    @Getter
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