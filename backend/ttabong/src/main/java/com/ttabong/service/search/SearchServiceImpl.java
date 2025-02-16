package com.ttabong.service.search;

import com.ttabong.dto.search.RecruitRequestDto;
import com.ttabong.dto.search.RecruitResponseDto;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.util.DateTimeUtil;
import com.ttabong.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
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
        Pageable pageable = PageRequest.of(0, limit, Sort.by("id").ascending());

        List<Recruit> recruits = recruitRepository.searchRecruits(
                requestDto.getRecruitTitle(),
                requestDto.getStatus(),
                requestDto.getRegion(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                cursor,
                pageable
        );

        List<RecruitResponseDto.TemplateDto> templates = recruits.stream()
                .sorted(Comparator.comparing((Recruit r) -> r.getTemplate().getId()).reversed())
                .map(recruit -> {
                    var template = recruit.getTemplate();
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

                    return RecruitResponseDto.TemplateDto.builder()
                            .templateId(template.getId())
                            .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                            .title(template.getTitle())
                            .activityLocation(template.getActivityLocation())
                            .status(recruit.getStatus())
                            .imageId(imageUrl)
                            .contactName(template.getContactName())
                            .contactPhone(template.getContactPhone())
                            .description(template.getDescription())
                            .createdAt(createdAt)
                            .group(RecruitResponseDto.GroupDto.builder()
                                    .groupId(group.getId())
                                    .groupName(group.getGroupName())
                                    .build())
                            .organization(RecruitResponseDto.OrganizationDto.builder()
                                    .orgId(org != null ? org.getId() : null)
                                    .orgName(org != null ? org.getOrgName() : null)
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        Integer nextCursor = templates.isEmpty() ? null : templates.get(templates.size() - 1).getTemplateId();

        return RecruitResponseDto.builder()
                .templates(templates)
                .nextCursor(nextCursor)
                .build();
    }

}
