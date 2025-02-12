package com.ttabong.dto.recruit.responseDto.vol;

import com.ttabong.dto.user.OrganizationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ReadVolRecruitsListResponseDto {
    private List<TemplateWrapper> templates; // ✅ 템플릿을 감싸는 객체 리스트


    @Getter
    @AllArgsConstructor
    public static class TemplateWrapper {
        private ReadVolRecruitsResponseDto template;
        private GroupDto group;
        private OrganizationDto organization;
    }
}
