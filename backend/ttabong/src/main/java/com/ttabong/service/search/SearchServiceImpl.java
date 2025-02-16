package com.ttabong.service.search;

import com.ttabong.dto.search.RecruitRequestDto;
import com.ttabong.dto.search.RecruitResponseDto;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final RecruitRepository recruitRepository;
    private final ImageUtil imageUtil;

    @Override
    public RecruitResponseDto searchTemplates(RecruitRequestDto requestDto, Long cursor, int limit) {
        List<Recruit> recruits = recruitRepository.searchRecruits(
                requestDto.getRecruitTitle(),
                requestDto.getStatus(),
                requestDto.getRegion(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                cursor,
                limit
        );
//        String profileImageUrl = imageUtil.getPresignedDownloadUrl(writer.getProfileImage());
//        if (writer.getProfileImage() != null) {
//            try {
//                profileImageUrl = imageUtil.getPresignedDownloadUrl(writer.getProfileImage());
//            } catch (Exception e) {
//                profileImageUrl = null;
//            }
//        }

        List<RecruitResponseDto.TemplateDto> templates = recruits.stream()
                .map(recruit -> RecruitResponseDto.TemplateDto.builder()
                        .templateId(recruit.getTemplate().getId())
                        .categoryId(recruit.getTemplate().getCategory() != null ? recruit.getTemplate().getCategory().getId() : null)
                        .title(recruit.getTemplate().getTitle())
                        .activityLocation(recruit.getTemplate().getActivityLocation())
                        .status(recruit.getStatus())
//                        .imageId(imageUtil.getPresignedDownloadUrl(writer.getProfileImage())
//                                ? imageUtil.getPresignedDownloadUrl(recruit.getTemplate().getThumbnailImage().getObjectPath())
//                                : null)
                        .contactName(recruit.getTemplate().getContactName())
                        .contactPhone(recruit.getTemplate().getContactPhone())
                        .description(recruit.getTemplate().getDescription())
                        .createdAt(LocalDateTime.from(recruit.getTemplate().getCreatedAt()))
                        .group(RecruitResponseDto.GroupDto.builder()
                                .groupId(recruit.getTemplate().getGroup().getId())
                                .groupName(recruit.getTemplate().getGroup().getGroupName())
                                .build())
                        .organization(RecruitResponseDto.OrganizationDto.builder()
                                .orgId(recruit.getTemplate().getOrg() != null ? recruit.getTemplate().getOrg().getId() : null)
                                .orgName(recruit.getTemplate().getOrg() != null ? recruit.getTemplate().getOrg().getOrgName() : null)
                                .build())
                        .build())
                .collect(Collectors.toList());

        Integer nextCursor = templates.isEmpty() ? null : templates.get(templates.size() - 1).getTemplateId();

        return RecruitResponseDto.builder()
                .templates(templates)
                .nextCursor(nextCursor)
                .build();
    }
}
