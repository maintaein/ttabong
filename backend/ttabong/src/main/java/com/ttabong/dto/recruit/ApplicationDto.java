package com.ttabong.dto.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {

    private Long applicationId;
    private Long volunteerId;
    private Long recruitId;
    private String status;
    private Long evaluationDone;
    private Long isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
