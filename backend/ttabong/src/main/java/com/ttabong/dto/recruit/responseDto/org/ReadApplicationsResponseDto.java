package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

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
    @Setter
    public static class ApplicationDetail {
        private User user;
        private Volunteer volunteer;
        private Application application;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class User {
        private Integer userId;
        private String email;
        private String name;
        private String profileImage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Volunteer {
        private Integer volunteerId;
        private Integer recommendedCount;
        private Integer totalVolunteerHours;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Application {
        private Integer applicationId;
        private Integer recruitId;
        private String status;
        private LocalDateTime createdAt;
    }
}