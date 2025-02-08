package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTemplateResponse {
    private String message;
    private Integer templateId;
    private Integer orgId;
}
