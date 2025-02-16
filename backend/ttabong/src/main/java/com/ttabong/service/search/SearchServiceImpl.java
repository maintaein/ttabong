package com.ttabong.service.search;

import com.ttabong.dto.search.RecruitRequestDto;
import com.ttabong.dto.search.RecruitResponseDto;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.util.DateTimeUtil;
import com.ttabong.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final RecruitRepository recruitRepository;
    private final ImageUtil imageUtil;

    @Override
    public RecruitResponseDto searchTemplates(RecruitRequestDto requestDto, Integer cursor, Integer limit) {
        Instant currentDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<Recruit> recruits = recruitRepository.searchRecruits(
                requestDto.getRecruitTitle(),
                requestDto.getStatus(),
                requestDto.getRegion(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                cursor,
                currentDate,
                PageRequest.of(0, limit)
        );

        Map<Integer, List<Recruit>> recruitMap = recruits.stream()
                .collect(Collectors.groupingBy(r -> r.getTemplate().getId()));

        List<RecruitResponseDto.TemplateDto> templates = recruitMap.entrySet().stream()
                .map(entry -> {
                    Recruit sampleRecruit = entry.getValue().get(0);
                    var template = sampleRecruit.getTemplate();
                    var group = template.getGroup();
                    var org = template.getOrg();

                    LocalDateTime createdAt = template.getCreatedAt() != null ?
                            DateTimeUtil.convertToLocalDateTime(template.getCreatedAt()) : null;

                    String imageUrl = Optional.ofNullable(template.getThumbnailImage())
                            .map(image -> {
                                try {
                                    return imageUtil.getPresignedDownloadUrl(image.getImageUrl());
                                } catch (Exception e) {
                                    return null;
                                }
                            })
                            .orElse(null);

                    List<RecruitResponseDto.RecruitDto> recruitDtos = entry.getValue().stream()
                            .map(r -> RecruitResponseDto.RecruitDto.builder()
                                    .recruitId(r.getId())
                                    .activityDate(DateTimeUtil.convertToLocalDate(r.getActivityDate()))
                                    .deadline(r.getDeadline())
                                    .activityStart(r.getActivityStart())
                                    .activityEnd(r.getActivityEnd())
                                    .maxVolunteer(r.getMaxVolunteer())
                                    .participateVolCount(r.getParticipateVolCount())
                                    .status(r.getStatus())
                                    .updatedAt(r.getUpdatedAt() != null ? DateTimeUtil.convertToLocalDateTime(r.getUpdatedAt()) : null)
                                    .createdAt(r.getCreatedAt() != null ? DateTimeUtil.convertToLocalDateTime(r.getCreatedAt()) : null)
                                    .build())
                            .collect(Collectors.toList());

                    return RecruitResponseDto.TemplateDto.builder()
                            .templateId(template.getId())
                            .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                            .title(template.getTitle())
                            .activityLocation(template.getActivityLocation())
                            .status(sampleRecruit.getStatus())
                            .imageUrl(imageUrl)
                            .contactName(template.getContactName())
                            .contactPhone(template.getContactPhone())
                            .description(template.getDescription())
                            .createdAt(createdAt)
                            .organization(RecruitResponseDto.OrganizationDto.builder()
                                    .orgId(org != null ? org.getId() : null)
                                    .orgName(org != null ? org.getOrgName() : null)
                                    .build())
                            .group(RecruitResponseDto.GroupDto.builder()
                                    .groupId(group.getId())
                                    .groupName(group.getGroupName())
                                    .build())
                            .recruits(recruitDtos)
                            .build();
                })
                .sorted(Comparator.comparing(RecruitResponseDto.TemplateDto::getTemplateId).reversed())
                .collect(Collectors.toList());

        Integer nextCursor = templates.isEmpty() ? null : templates.get(templates.size() - 1).getTemplateId();

        return RecruitResponseDto.builder()
                .templates(templates)
                .nextCursor(nextCursor)
                .build();
    }

}
