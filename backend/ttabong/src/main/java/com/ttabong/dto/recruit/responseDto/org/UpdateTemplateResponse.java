package com.ttabong.dto.recruit.responseDto.org;

import com.ttabong.dto.GlobalBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTemplateResponse implements GlobalBaseDto {
    private String message;
    private Integer templateId;
    private Integer orgId;
}
