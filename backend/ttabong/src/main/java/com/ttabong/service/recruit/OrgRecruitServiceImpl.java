package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.ReadAvailableRecruitsResponseDto.*;
import com.ttabong.dto.recruit.responseDto.org.ReadMyRecruitsResponseDto.*;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.recruit.TemplateGroup;
import com.ttabong.entity.recruit.TemplateImage;
import com.ttabong.entity.sns.ReviewImage;
import com.ttabong.entity.user.Organization;
import com.ttabong.repository.recruit.*;
import com.ttabong.repository.sns.ReviewImageRepository;
import com.ttabong.repository.user.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
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
    private final CategoryRepository categoryRepository;
    private final TemplateImageRepository templateImageRepository;


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

    @Override
    public UpdateTemplateResponse updateTemplate(UpdateTemplateRequestDto updateTemplateDto) {

        // 조직 ID를 이용해 Organization 객체를 조회
        Organization org = organizationRepository.findById(updateTemplateDto.getOrgId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        // 템플릿을 업데이트
        templateRepository.updateTemplate(updateTemplateDto.getTemplateId(), org, updateTemplateDto.getTitle(),
                updateTemplateDto.getDescription(), updateTemplateDto.getActivityLocation(),
                updateTemplateDto.getContactName(), updateTemplateDto.getContactPhone());

        // 응답 DTO 생성
        return UpdateTemplateResponse.builder()
                .message("템플릿 수정 성공")
                .templateId(updateTemplateDto.getTemplateId())
                .orgId(updateTemplateDto.getOrgId())
                .build();
    }

    @Override
    @Transactional
    public DeleteTemplatesResponseDto deleteTemplates(DeleteTemplatesRequestDto deleteTemplatesDto) {
        // 템플릿 ID 리스트를 받아 삭제
        List<Integer> deleteTemplateIds = deleteTemplatesDto.getDeletedTemplates();

        // 템플릿 삭제 (DB에서 논리적으로 삭제)
        templateRepository.deleteTemplates(deleteTemplateIds);

        // 응답 DTO 생성
        return DeleteTemplatesResponseDto.builder()
                .message("템플릿 삭제 성공")
                .deletedTemplates(deleteTemplateIds)  // 삭제된 템플릿 ID 리스트 전달
                .build();
    }

    @Override
    public DeleteGroupResponseDto deleteGroup(DeleteGroupDto deleteGroupDto) {
        // 삭제할 그룹 ID와 기관 ID를 받아서 그룹을 삭제하는 로직 구현
        Integer groupId = deleteGroupDto.getGroupId();
        Integer orgId = deleteGroupDto.getOrgId();

        // 해당 그룹을 논리 삭제 (is_deleted = true로 업데이트)
        templateGroupRepository.deleteGroupByIdAndOrg(groupId, orgId);

        // 응답 DTO 반환
        return DeleteGroupResponseDto.builder()
                .message("삭제 성공")
                .groupId(groupId)
                .orgId(orgId)
                .build();
    }

    @Override
    public ReadTemplatesResponseDto readTemplates(int cursor, int limit) {

        // Pageable 생성: cursor가 페이지 번호(0부터 시작), limit가 한 페이지에 보여줄 데이터 수
        Pageable pageable = PageRequest.of(cursor, limit);

        // 그룹 조회 (페이지네이션 적용)
        List<TemplateGroup> groups = templateGroupRepository.findGroups(pageable);

        // 그룹별 템플릿 조회 및 dto 변환
        List<ReadTemplatesResponseDto.GroupDto> groupDtos = groups.stream()
                .map(group -> {
                    // 그룹 dto 생성
                    ReadTemplatesResponseDto.GroupDto groupDto = ReadTemplatesResponseDto.GroupDto.builder()
                            .groupId(group.getId())
                            .groupName(group.getGroupName())
                            .templates(
                                    // 그룹에 속한 템플릿 목록 조회
                                    templateRepository.findTemplatesByGroupId(group.getId()).stream()
                                            .map(template -> ReadTemplatesResponseDto.TemplateDto.builder()
                                                    .templateId(template.getId())
                                                    .orgId(template.getOrg().getId())
                                                    .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                                                    .title(template.getTitle())
                                                    .activityLocation(template.getActivityLocation())
                                                    .status(template.getStatus())
                                                    .imageId(template.getImageId())
                                                    .contactName(template.getContactName())
                                                    .contactPhone(template.getContactPhone())
                                                    .description(template.getDescription())
                                                    .createdAt(template.getCreatedAt() != null
                                                            ? template.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                                                            : null)
                                                    .build()
                                            ).collect(Collectors.toList())
                            )
                            .build();

                    return groupDto;
                })
                .collect(Collectors.toList());

        return ReadTemplatesResponseDto.builder()
                .groups(groupDtos)
                .build();
    }

    // TODO: 이미지 저장하기 (지금은 임시로 Template_image 테이블 하나 더 만들어서 사용중)
    @Override
    public CreateTemplateResponseDto createTemplate(CreateTemplateRequestDto createTemplateDto) {

        // 요청받은 데이터를 바탕으로 Template 엔티티 생성
        Template template = Template.builder()
                .group(templateGroupRepository.findById(createTemplateDto.getGroupId())
                        .orElseThrow(() -> new IllegalArgumentException("Group not found")))
                .org(organizationRepository.findById(createTemplateDto.getOrgId())
                        .orElseThrow(() -> new IllegalArgumentException("Organization not found")))
                .category(categoryRepository.findById(createTemplateDto.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Category not found")))
                .title(createTemplateDto.getTitle())
                .activityLocation(createTemplateDto.getActivityLocation())
                .status(createTemplateDto.getStatus())
                .contactName(createTemplateDto.getContactName())
                .contactPhone(createTemplateDto.getContactPhone())
                .description(createTemplateDto.getDescription())
                .isDeleted(false)
                .createdAt(Instant.now()) // createdAt 필드는 Instant 타입으로 설정
                .build();

        // Template을 디비에 저장
        Template savedTemplate = templateRepository.save(template);

        // 이미지 처리: 받은 이미지 리스트에서 각 이미지를 TemplateImage 테이블에 저장
        if (createTemplateDto.getImages() != null && !createTemplateDto.getImages().isEmpty()) {
            List<TemplateImage> templateImages = createTemplateDto.getImages().stream()
                    .map(imageUrl -> TemplateImage.builder()
                            .template(savedTemplate)  // 템플릿과 연결
                            .imageUrl(imageUrl)
                            .createdAt(Instant.now())
                            .build())
                    .collect(Collectors.toList());


            templateImageRepository.saveAll(templateImages);
        }
        
        return CreateTemplateResponseDto.builder()
                .message("템플릿 생성 성공")
                .templateId(savedTemplate.getId())
                .build();
    }

    @Override
    public CreateGroupResponseDto createGroup(CreateGroupRequestDto createGroupDto) {

        // orgId에 해당하는게 있는지 없는지
        Organization org = organizationRepository.findById(createGroupDto.getOrgId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        // 그룹 객체 생성
        TemplateGroup newGroup = TemplateGroup.builder()
                .org(org)
                .groupName(createGroupDto.getGroupName())
                .isDeleted(false)  // 기본값으로 false로 설정
                .build();

        // 그룹 저장
        TemplateGroup savedGroup = templateGroupRepository.save(newGroup);

        return CreateGroupResponseDto.builder()
                .message("그룹 생성 성공")
                .groupId(savedGroup.getId())  // 생성된 그룹의 ID 반환
                .build();
    }

    @Override
    public CreateRecruitResponseDto createRecruit(CreateRecruitRequestDto createRecruitDto) {

        // 마감일이 LocalDateTime인데 이를 Instant로 바꾸기
        Instant deadlineInstant = createRecruitDto.getDeadline() != null
                ? createRecruitDto.getDeadline().atZone(ZoneId.systemDefault()).toInstant()
                : null;

        // 템플릿 id로 템플릿 엔티티 찾기
        Template template = templateRepository.findById(createRecruitDto.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("해당 템플릿 없습니다"));

        // Recruit 엔티티 생성
        Recruit recruit = Recruit.builder()
                .template(template)
                .deadline(deadlineInstant)
                .activityDate(createRecruitDto.getActivityDate())
                .activityStart(createRecruitDto.getActivityStart())
                .activityEnd(createRecruitDto.getActivityEnd())
                .maxVolunteer(createRecruitDto.getMaxVolunteer())
                .status("RECRUITING")  // 기본값으로 'RECRUITING' 넣기
                .isDeleted(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // 공고 저장
        recruit = recruitRepository.save(recruit);

        return CreateRecruitResponseDto.builder()
                .message("공고 생성 완료")
                .recruitId(recruit.getId())
                .build();
    }

    @Override
    public ReadRecruitResponseDto readRecruit(int recruitId) {
        // Recruit 엔티티 조회
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new RuntimeException("해당 공고가 없습니다"));

        // Instant를 LocalDateTime으로 변환 (UTC -> 시스템 기본 시간대)
        LocalDateTime deadlineLocalDateTime = recruit.getDeadline() != null
                ? LocalDateTime.ofInstant(recruit.getDeadline(), ZoneId.systemDefault())
                : null;

        // Date를 LocalDate로 변환 (Date -> LocalDate)
        LocalDate activityLocalDate = recruit.getActivityDate() != null
                ? recruit.getActivityDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;

        LocalDateTime createdAtLocalDateTime = recruit.getTemplate().getCreatedAt() != null
                ? LocalDateTime.ofInstant(recruit.getTemplate().getCreatedAt(), ZoneId.systemDefault())
                : null;

        // recruit부분 값 설정하기
        ReadRecruitResponseDto.Recruit recruitDto = ReadRecruitResponseDto.Recruit.builder()
                .recruitId(recruit.getId())
                .deadline(deadlineLocalDateTime)  // 변환된 LocalDateTime 사용
                .activityDate(activityLocalDate)  // 변환된 LocalDate 사용
                .activityStart(recruit.getActivityStart() != null ? recruit.getActivityStart() : BigDecimal.ZERO)  // BigDecimal 유지
                .activityEnd(recruit.getActivityEnd() != null ? recruit.getActivityEnd() : BigDecimal.ZERO)        // BigDecimal 유지
                .maxVolunteer(recruit.getMaxVolunteer())
                .participateVolCount(recruit.getParticipateVolCount())
                .status(recruit.getStatus())
                .updatedAt(LocalDateTime.ofInstant(recruit.getUpdatedAt(), ZoneId.systemDefault()))  // Instant -> LocalDateTime
                .createdAt(LocalDateTime.ofInstant(recruit.getCreatedAt(), ZoneId.systemDefault()))  // Instant -> LocalDateTime
                .build();


        // Group, Template, Organization 등의 다른 정보를 추가로 설정해야 할 경우
        ReadRecruitResponseDto.Group groupDto = new ReadRecruitResponseDto.Group(
                recruit.getTemplate().getGroup().getId(),
                recruit.getTemplate().getGroup().getGroupName()
        );

        ReadRecruitResponseDto.Template templateDto = ReadRecruitResponseDto.Template.builder()
                .templateId(recruit.getTemplate().getId())
                .categoryId(recruit.getTemplate().getCategory().getId())
                .title(recruit.getTemplate().getTitle())
                .activityLocation(recruit.getTemplate().getActivityLocation())
                .status(recruit.getTemplate().getStatus())
                .imageId(recruit.getTemplate().getImageId())
                .contactName(recruit.getTemplate().getContactName())
                .contactPhone(recruit.getTemplate().getContactPhone())
                .description(recruit.getTemplate().getDescription())
                .createdAt(createdAtLocalDateTime)  // 변환된 LocalDateTime 사용
                .build();


        ReadRecruitResponseDto.Organization orgDto = new ReadRecruitResponseDto.Organization(
                recruit.getTemplate().getOrg().getId(),
                recruit.getTemplate().getOrg().getOrgName()
        );

        return ReadRecruitResponseDto.builder()
                .group(groupDto)
                .template(templateDto)
                .recruit(recruitDto)
                .organization(orgDto)
                .build();
    }


}
