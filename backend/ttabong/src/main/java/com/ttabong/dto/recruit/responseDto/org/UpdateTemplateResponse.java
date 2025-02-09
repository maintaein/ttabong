package com.ttabong.dto.recruit.responseDto.org;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTemplateResponse {
    private String message;
    private Integer templateId;
    private Integer orgId;
}
