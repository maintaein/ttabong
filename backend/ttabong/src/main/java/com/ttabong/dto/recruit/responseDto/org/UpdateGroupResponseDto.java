package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGroupResponseDto {
    private String message;
    private Integer groupId;
    private Integer orgId;
}
