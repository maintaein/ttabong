package com.ttabong.dto.recruit;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Long applicationId;
    private Long volunteerId;
    private Long recruitId;
    private String status;
    private Boolean evaluationDone;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
