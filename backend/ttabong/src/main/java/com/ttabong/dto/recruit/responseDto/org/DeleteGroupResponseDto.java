package com.ttabong.dto.recruit.responseDto.org;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteGroupResponseDto {
    String message;
    Integer groupId;
    Integer orgId;
}
