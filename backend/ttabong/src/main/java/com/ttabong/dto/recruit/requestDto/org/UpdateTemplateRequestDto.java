package com.ttabong.dto.recruit.requestDto.org;

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
public class UpdateTemplateRequestDto implements GlobalBaseDto {
    private Integer templateId;
    private Integer groupId;
    private Integer orgId;
    private Integer categoryId;
    private String title;
    private String activityLocation;
    private String status;
    private List<String> images;
    private Integer imageCount;
    private String contactName;
    private String contactPhone;
    private String description;
}
