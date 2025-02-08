package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.requestDto.org.CloseRecruitRequestDto;
import com.ttabong.dto.recruit.requestDto.org.DeleteRecruitsRequestDto;
import com.ttabong.dto.recruit.requestDto.org.UpdateGroupRequestDto;
import com.ttabong.dto.recruit.requestDto.org.UpdateRecruitsRequestDto;
import com.ttabong.dto.recruit.responseDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.ReadAvailableRecruitsResponseDto.*;
import com.ttabong.dto.recruit.responseDto.org.ReadMyRecruitsResponseDto.*;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.recruit.TemplateGroup;
import com.ttabong.entity.user.Organization;
import com.ttabong.repository.recruit.RecruitRepository;
import com.ttabong.repository.recruit.TemplateGroupRepository;
import com.ttabong.repository.recruit.TemplateRepository;
import com.ttabong.repository.user.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
@Transactional
@Slf4j
public class OrgRecruitServiceImpl implements OrgRecruitService {

    private final RecruitRepository recruitRepository;
    private final TemplateRepository templateRepository;
    private final TemplateGroupRepository templateGroupRepository;
    private final OrganizationRepository organizationRepository;


    // TODO: 마지막 공고까지 다 로드했다면? & db에서 정보 누락된게 있다면? , 삭제여부 확인, 마감인건 빼고 가져오기
    @Override
    @Transactional(readOnly = true)
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

    // TODO: 마지막 공고까지 다 로드했다면? & db에서 정보 누락된게 있다면?, 삭제여부 확인
    @Override
    @Transactional(readOnly = true)
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

    // TODO: 이미 삭제된 공고는 어떻게 처리? 삭제 실패시 처리
    @Override
    public DeleteRecruitsResponseDto deleteRecruits(DeleteRecruitsRequestDto deleteRecruitDto) {

        List<Integer> recruitIds = deleteRecruitDto.getDeletedRecruits();

        recruitRepository.markAsDeleted(recruitIds);

        return DeleteRecruitsResponseDto.builder()
                .message("공고 삭제 완료")
                .deletedRecruits(recruitIds)
                .build();
    }

    @Override
    public UpdateRecruitsResponseDto updateRecruit(Integer recruitId, UpdateRecruitsRequestDto requestDto) {

        Instant deadlineInstant = requestDto.getDeadline() != null
                ? requestDto.getDeadline().atZone(ZoneId.systemDefault()).toInstant()
                : null;

        recruitRepository.updateRecruit(
                recruitId,
                deadlineInstant,
                requestDto.getActivityDate(),
                requestDto.getActivityStart(),
                requestDto.getActivityEnd(),
                requestDto.getMaxVolunteer()
        );

        return UpdateRecruitsResponseDto.builder()
                .message("공고 수정 완료")
                .recruitId(recruitId)
                .build();
    }

    // TODO: db에 있는지 보고, 마감으로 수정하기
    @Override
    public CloseRecruitResponseDto closeRecruit(CloseRecruitRequestDto closeRecruitDto) {
        // 마감할 공고
        Integer recruitId = closeRecruitDto.getRecruitId();

        // 공고 찾아서 마감처리하기
//        Recruit recruit = recruitRepository.findById(recruitId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 공고가 존재하지 않습니다"));

//        recruit.close();
//        recruitRepository.save(recruit);

        recruitRepository.closeRecruit(recruitId);


        return CloseRecruitResponseDto.builder()
                .message("공고 마감 완료")
                .recruitId(recruitId)
                .build();
    }

    @Override
    public UpdateGroupResponseDto updateGroup(UpdateGroupRequestDto updateGroupDto) {

        // 토큰 인증 할거지만, 일단 기관까지 그냥 체크해주자 +그룹id로 하자
        Organization org = organizationRepository.findById(updateGroupDto.getOrgId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        templateGroupRepository.updateGroup(updateGroupDto.getGroupId(), org, updateGroupDto.getGroupName());

        return UpdateGroupResponseDto.builder()
                .message("수정 성공")
                .groupId(updateGroupDto.getGroupId())
                .orgId(updateGroupDto.getOrgId())
                .build();
    }

}
