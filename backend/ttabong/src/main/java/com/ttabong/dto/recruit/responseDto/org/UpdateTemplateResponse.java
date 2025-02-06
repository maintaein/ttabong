package com.ttabong.dto.recruit.responseDto.org;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTemplateResponse {
    String message;
    Integer templateId;
    Integer orgId;
}