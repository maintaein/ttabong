package com.ttabong.dto.recruit.responseDto.vol;

import com.ttabong.dto.user.OrganizationDto;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadRecruitDetailResponseDto {
    private TemplateDto template;
    private GroupDto group;
    private CategoryDto category;
    private OrganizationDto organization;
    private List<ReadRecruitDetailResponseDto.RecruitDto> recruits; // ✅ 올바른 DTO 사용

    public static ReadRecruitDetailResponseDto from(Template template) {
        return ReadRecruitDetailResponseDto.builder()
                .template(TemplateDto.from(template))
                .group(GroupDto.from(template.getGroup()))
                .category(CategoryDto.from(template.getCategory()))
                .organization(OrganizationDto.from(template.getOrg()))
                .recruits(template.getRecruits().stream()
                        .filter(recruit -> !Boolean.TRUE.equals(recruit.getIsDeleted())) // Soft Delete 필터링
                        .map(ReadRecruitDetailResponseDto.RecruitDto::from) // ✅ 올바른 DTO 명시
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TemplateDto {
        private Integer templateId;
        private Integer categoryId;
        private String title;
        private String activityLocation;
        private String status;
        private List<String> images;
        private String contactName;
        private String contactPhone;
        private String description;
        private Instant createdAt;

        public static TemplateDto from(Template template) {
            return TemplateDto.builder()
                    .templateId(template.getId())
                    .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                    .title(template.getTitle())
                    .activityLocation(template.getActivityLocation())
                    .status(template.getStatus())
                    .images(template.getImages() != null
                            ? template.getImages().stream().map(image -> image.getImageUrl()).collect(Collectors.toList())
                            : List.of()) // ✅ Null 체크 추가
                    .contactName(template.getContactName())
                    .contactPhone(template.getContactPhone())
                    .description(template.getDescription())
                    .createdAt(template.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecruitDto {
        private Integer recruitId;
        private Instant deadline;
        private Instant activityDate;
        private Integer activityStart;
        private Integer activityEnd;
        private Integer maxVolunteer;
        private Integer participateVolCount;
        private String status;
        private Instant updatedAt;
        private Instant createdAt;

        public static RecruitDto from(Recruit recruit) {
            return RecruitDto.builder()
                    .recruitId(recruit.getId())
                    .deadline(recruit.getDeadline())
                    .activityDate(recruit.getActivityDate().toInstant())
                    .activityStart(recruit.getActivityStart().intValue())
                    .activityEnd(recruit.getActivityEnd().intValue())
                    .maxVolunteer(recruit.getMaxVolunteer())
                    .participateVolCount(recruit.getParticipateVolCount())
                    .status(recruit.getStatus())
                    .updatedAt(recruit.getUpdatedAt())
                    .createdAt(recruit.getCreatedAt())
                    .build();
        }
    }
}
