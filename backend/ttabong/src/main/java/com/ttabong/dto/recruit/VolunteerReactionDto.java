package com.ttabong.dto.recruit;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerReactionDto {
    private Long reactionId;
    private Long volunteerId;
    private Long recruitId;
    private Boolean isLike;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
}
