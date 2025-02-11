package com.ttabong.dto.recruit.responseDto.vol;

import com.ttabong.dto.user.OrganizationDto;
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
    private ReadVolRecruitsResponseDto template;
    private GroupDto group;
    private OrganizationDto organization;
    private List<RecruitDto> recruits;

    public static ReadRecruitDetailResponseDto from(Template template) {
        return ReadRecruitDetailResponseDto.builder()
                .template(ReadVolRecruitsResponseDto.from(template)) // ✅ 변경: 기존 builder 제거하고 from() 메서드 활용
                .group(GroupDto.from(template.getGroup()))  // ✅ 변경된 부분
                .organization(template.getOrg() != null ? OrganizationDto.from(template.getOrg()) : null)
                .recruits(template.getRecruits().stream()
                        .filter(recruit -> !Boolean.TRUE.equals(recruit.getIsDeleted())) // Soft Delete 필터링
                        .map(recruit -> RecruitDto.builder()
                                .recruitId(recruit.getId())
                                .deadline(recruit.getDeadline())
                                .activityDate(Instant.ofEpochMilli(recruit.getActivityDate().getTime())) // ✅ 변경
                                .activityStart(recruit.getActivityStart().intValue()) // ✅ 변경
                                .activityEnd(recruit.getActivityEnd().intValue()) // ✅ 변경
                                .maxVolunteer(recruit.getMaxVolunteer())
                                .participateVolCount(recruit.getParticipateVolCount())
                                .status(recruit.getStatus())
                                .updatedAt(recruit.getUpdatedAt())
                                .createdAt(recruit.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
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
    }
}
