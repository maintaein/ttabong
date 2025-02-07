package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.responseDto.org.ReadAvailableRecruitsResponseDto;
import com.ttabong.dto.recruit.responseDto.org.ReadAvailableRecruitsResponseDto.*;
import com.ttabong.dto.recruit.responseDto.org.ReadMyRecruitsResponseDto;
import com.ttabong.dto.recruit.responseDto.org.ReadMyRecruitsResponseDto.*;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.recruit.TemplateGroup;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.repository.recruit.TemplateGroupRepository;
import com.ttabong.repository.recruit.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrgRecruitServiceImpl implements OrgRecruitService {

    private final RecruitRepository recruitRepository;
    private final TemplateRepository templateRepository;
    private final TemplateGroupRepository templateGroupRepository;


    // TODO: 마지막 공고까지 다 로드했다면? & db에서 정보 누락된게 있다면?
    @Override
    public ReadAvailableRecruitsResponseDto readAvailableRecruits(Integer cursor, Integer limit) {
        List<Template> templates = templateRepository.findAvailableTemplates(cursor, limit);

        List<TemplateDetail> templateDetails = templates.stream().map(template -> {
            // 그룹 정보 가져오기
            TemplateGroup templateGroup = template.getGroup();
            ReadAvailableRecruitsResponseDto.Group group = Optional.ofNullable(templateGroup)
                    .map(g -> ReadAvailableRecruitsResponseDto.Group.builder()
                            .groupId(g.getId())
                            .groupName(g.getGroupName())
                            .build())
                    .orElse(null);

            // 모집 정보 가져오기
            List<Recruit> recruitEntities = recruitRepository.findByTemplateId(template.getId());
            List<ReadAvailableRecruitsResponseDto.Recruit> recruits = recruitEntities.stream()
                    .map(recruit -> ReadAvailableRecruitsResponseDto.Recruit.builder()
                            .recruitId(recruit.getId())
                            .deadline(recruit.getDeadline() != null ?
                                    LocalDateTime.ofInstant(recruit.getDeadline(), java.time.ZoneId.systemDefault()) : null)
                            .activityDate(recruit.getActivityDate() != null ?
                                    recruit.getActivityDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null)
                            .activityStart(recruit.getActivityStart() != null ?
                                    recruit.getActivityStart().intValue() : null)
                            .activityEnd(recruit.getActivityEnd() != null ?
                                    recruit.getActivityEnd().intValue() : null)
                            .maxVolunteer(recruit.getMaxVolunteer())
                            .participateVolCount(recruit.getParticipateVolCount())
                            .status(recruit.getStatus())
                            .updatedAt(recruit.getUpdatedAt() != null ?
                                    LocalDateTime.ofInstant(recruit.getUpdatedAt(), java.time.ZoneId.systemDefault()) : null)
                            .createdAt(recruit.getCreatedAt() != null ?
                                    LocalDateTime.ofInstant(recruit.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
                            .build())
                    .collect(Collectors.toList());

            return TemplateDetail.builder()
                    .template(ReadAvailableRecruitsResponseDto.Template.builder()
                            .templateId(template.getId())
                            .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                            .title(template.getTitle())
                            .activityLocation(template.getActivityLocation())
                            .status(template.getStatus())
                            .imageId(template.getImageId())
                            .contactName(template.getContactName())
                            .contactPhone(template.getContactPhone())
                            .description(template.getDescription())
                            .createdAt(template.getCreatedAt() != null ?
                                    LocalDateTime.ofInstant(template.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
                            .build())
                    .group(group)
                    .recruits(recruits)
                    .build();
        }).collect(Collectors.toList());

        return ReadAvailableRecruitsResponseDto.builder()
                .templates(templateDetails)
                .build();
    }

    // TODO: 마지막 공고까지 다 로드했다면? & db에서 정보 누락된게 있다면?
    @Override
    public ReadMyRecruitsResponseDto readMyRecruits(Integer cursor, Integer limit) {
        List<Recruit> recruits = recruitRepository.findAvailableRecruits(cursor, limit);

        List<RecruitDetail> recruitDetails = recruits.stream().map(recruit -> {
            // 그룹 정보 가져오기
            Template template = recruit.getTemplate();
            TemplateGroup templateGroup = template.getGroup();

            ReadMyRecruitsResponseDto.Group group = Optional.ofNullable(templateGroup)
                    .map(g -> ReadMyRecruitsResponseDto.Group.builder()
                            .groupId(g.getId())
                            .groupName(g.getGroupName())
                            .build())
                    .orElse(null);

            ReadMyRecruitsResponseDto.Template dtoTemplate = ReadMyRecruitsResponseDto.Template.builder()
                    .templateId(template.getId())
                    .title(template.getTitle())
                    .build();

            ReadMyRecruitsResponseDto.Recruit dtoRecruit = ReadMyRecruitsResponseDto.Recruit.builder()
                    .recruitId(recruit.getId())
                    .status(recruit.getStatus())
                    .maxVolunteer(recruit.getMaxVolunteer())
                    .participateVolCount(recruit.getParticipateVolCount())
                    .activityDate(recruit.getActivityDate() != null ?
                            recruit.getActivityDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null)
                    .activityStart(recruit.getActivityStart() != null ?
                            recruit.getActivityStart().doubleValue() : null)
                    .activityEnd(recruit.getActivityEnd() != null ?
                            recruit.getActivityEnd().doubleValue() : null)
                    .deadline(recruit.getDeadline() != null ?
                            LocalDateTime.ofInstant(recruit.getDeadline(), java.time.ZoneId.systemDefault()) : null)
                    .createdAt(recruit.getCreatedAt() != null ?
                            LocalDateTime.ofInstant(recruit.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
                    .build();

            return RecruitDetail.builder()
                    .group(group)
                    .template(dtoTemplate)
                    .recruit(dtoRecruit)
                    .build();
        }).collect(Collectors.toList());

        return ReadMyRecruitsResponseDto.builder()
                .recruits(recruitDetails)
                .build();
    }

}
