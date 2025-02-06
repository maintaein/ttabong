package com.ttabong.dto.recruit;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerReactionDto {

    private Long reactionId;
    private Long volunteerId;
    private Long recruitId;
    private Long isLike;
    private Long isDeleted;
    private LocalDateTime createdAt;
}
