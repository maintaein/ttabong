package com.ttabong.dto.recruit.responseDto.vol;

import com.ttabong.dto.user.OrganizationDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadVolRecruitsListResponseDto {
    private List<TemplateWrapper> templates;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TemplateWrapper {
        private TemplateDto template;
        private GroupDto group;
        private OrganizationDto organization;

        public static TemplateWrapper from(Template template) {
            return TemplateWrapper.builder()
                    .template(TemplateDto.from(template))
                    .group(GroupDto.from(template.getGroup()))
                    .organization(template.getOrg() != null ? OrganizationDto.from(template.getOrg()) : null)
                    .build();
        }
    }
}
