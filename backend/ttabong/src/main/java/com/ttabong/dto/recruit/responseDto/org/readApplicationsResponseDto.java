package com.ttabong.dto.recruit.responseDto.org;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class readApplicationsResponseDto {
    private int recruitId;
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
    public static class User {
        private int userId;
        private String email;
        private String name;
        private String profileImage;
    }

    @Getter
    @Setter
    public static class Volunteer {
        private int volunteerId;
        private int recommendedCount;
        private int totalVolunteerHours;
    }

    @Getter
    @Setter
    public static class Application {
        private int applicationId;
        private int recruitId;
        private String status;
        private LocalDateTime createdAt;
    }
}