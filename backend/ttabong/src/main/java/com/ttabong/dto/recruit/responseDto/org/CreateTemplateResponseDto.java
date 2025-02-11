package com.ttabong.dto.recruit.responseDto.org;

import com.ttabong.dto.GlobalBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTemplateResponseDto implements GlobalBaseDto {
    private String message;
    private Integer templateId;
    private List<String> images;
    private String imageUrl;
}
