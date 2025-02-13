package com.ttabong.service.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;
import com.ttabong.dto.user.AuthDto;
import com.ttabong.entity.recruit.Application;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.recruit.TemplateGroup;
import com.ttabong.entity.user.Organization;
import com.ttabong.exception.ForbiddenException;
import com.ttabong.exception.NotFoundException;
import com.ttabong.exception.UnauthorizedException;
import com.ttabong.repository.recruit.*;
import com.ttabong.repository.user.OrganizationRepository;
import com.ttabong.repository.user.VolunteerRepository;
import com.ttabong.util.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrgRecruitServiceImpl implements OrgRecruitService {

    private final RecruitRepository recruitRepository;
    private final TemplateRepository templateRepository;
    private final TemplateGroupRepository templateGroupRepository;
    private final OrganizationRepository organizationRepository;
    private final CategoryRepository categoryRepository;
    private final ApplicationRepository applicationRepository;
    private final VolunteerRepository volunteerRepository;
    private final ImageService imageService;

    public void checkOrgToken(AuthDto authDto) {
        if (authDto == null || authDto.getUserId() == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        else if (!"organization".equalsIgnoreCase(authDto.getUserType())) {
            throw new ForbiddenException("기관 계정으로 로그인을 해야 합니다.");
        }
    }

    @Transactional(readOnly = true)
    public ReadAvailableRecruitsResponseDto readAvailableRecruits(Integer cursor, Integer limit, AuthDto authDto) {
//        checkOrgToken(authDto);

        try {
            checkOrgToken(authDto);

            if (cursor == null || cursor == 0) { cursor = Integer.MAX_VALUE; }

            if (limit == null || limit == 0) { limit=10; }

            List<Template> templates = templateRepository.findAvailableTemplates(cursor, authDto.getUserId(), PageRequest.of(0, limit));

            if (templates.isEmpty()) {
                throw new NotFoundException("활성화된 템플릿이 없습니다.");
            }

            Map<Integer, List<Recruit>> recruitMap = templates.stream()
                    .map(template -> {
                        try {
                            return recruitRepository.findByTemplateId(template.getId());
                        } catch (Exception e) {
                            throw new NotFoundException("템플릿 ID " + template.getId() + "에 대한 모집 공고를 찾을 수 없습니다.");
                        }
                    })
                    .flatMap(List::stream)
                    .collect(Collectors.groupingBy(recruit -> recruit.getTemplate().getId()));

            Map<Integer, List<String>> imageMap = templates.stream()
                    .collect(Collectors.toMap(
                            Template::getId,
                            template -> {
                                try {
                                    return imageService.getImageUrls(template.getId(), true);
                                } catch (Exception e) {
                                    return List.of();
                                }
                            }
                    ));

            List<ReadAvailableRecruitsResponseDto.TemplateDetail> templateDetails = templates.stream().map(template -> {
                ReadAvailableRecruitsResponseDto.Group groupInfo = template.getGroup() != null ?
                        new ReadAvailableRecruitsResponseDto.Group(
                                template.getGroup().getId(),
                                template.getGroup().getGroupName()
                        ) : new ReadAvailableRecruitsResponseDto.Group(1, "봉사");

                List<Recruit> recruitEntities = recruitMap.getOrDefault(template.getId(), List.of());
                List<ReadAvailableRecruitsResponseDto.Recruit> recruits = recruitEntities.stream()
                        .map(recruit -> ReadAvailableRecruitsResponseDto.Recruit.builder()
                                .recruitId(recruit.getId())
                                .deadline(recruit.getDeadline() != null ?
                                        recruit.getDeadline().atZone(ZoneId.systemDefault()).toLocalDateTime()
                                        : LocalDateTime.now())
                                .activityDate(recruit.getActivityDate() != null ? recruit.getActivityDate() : new Date())
                                .activityStart(recruit.getActivityStart() != null ? recruit.getActivityStart() : BigDecimal.ZERO)
                                .activityEnd(recruit.getActivityEnd() != null ? recruit.getActivityEnd() : BigDecimal.ZERO)
                                .maxVolunteer(recruit.getMaxVolunteer())
                                .participateVolCount(recruit.getParticipateVolCount())
                                .status(recruit.getStatus())
                                .updatedAt(recruit.getUpdatedAt() != null ?
                                        recruit.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                                        : LocalDateTime.now())
                                .createdAt(recruit.getCreatedAt() != null ?
                                        recruit.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                                        : LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());

                List<String> imageUrls = imageMap.getOrDefault(template.getId(), List.of());
                String thumbnailImageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

                return ReadAvailableRecruitsResponseDto.TemplateDetail.builder()
                        .template(ReadAvailableRecruitsResponseDto.Template.builder()
                                .templateId(template.getId())
                                .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                                .title(template.getTitle())
                                .activityLocation(template.getActivityLocation())
                                .status(template.getStatus())
                                .imageUrl(thumbnailImageUrl)
                                .contactName(template.getContactName())
                                .contactPhone(template.getContactPhone())
                                .description(template.getDescription())
                                .createdAt(template.getCreatedAt() != null ?
                                        template.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                                        : LocalDateTime.now())
                                .build())
                        .group(groupInfo)
                        .recruits(recruits)
                        .build();
            }).toList();

            return ReadAvailableRecruitsResponseDto.builder()
                    .templates(templateDetails)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("모집 정보를 불러오는 중 오류가 발생했습니다.", e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ReadMyRecruitsResponseDto readMyRecruits(Integer cursor, Integer limit, AuthDto authDto) {

        checkOrgToken(authDto);

        if (cursor == null) {
            cursor = Integer.MAX_VALUE;
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));

        List<Recruit> recruits = recruitRepository.findAvailableRecruits(cursor, authDto.getUserId(), pageable);

        if (recruits.isEmpty()) {
            throw new NotFoundException("활성화된 모집 공고가 없습니다.");
        }

        List<ReadMyRecruitsResponseDto.RecruitDetail> recruitDetails = recruits.stream().map(recruit -> {
            Template template = recruit.getTemplate();
            TemplateGroup templateGroup = template.getGroup();

            ReadMyRecruitsResponseDto.Group group = templateGroup != null ?
                    ReadMyRecruitsResponseDto.Group.builder()
                            .groupId(templateGroup.getId())
                            .groupName(templateGroup.getGroupName())
                            .build()
                    : null;

            ReadMyRecruitsResponseDto.Template dtoTemplate = ReadMyRecruitsResponseDto.Template.builder()
                    .templateId(template.getId())
                    .title(template.getTitle())
                    .build();

            ReadMyRecruitsResponseDto.Recruit dtoRecruit = ReadMyRecruitsResponseDto.Recruit.builder()
                    .recruitId(recruit.getId())
                    .status(recruit.getStatus())
                    .maxVolunteer(recruit.getMaxVolunteer())
                    .participateVolCount(recruit.getParticipateVolCount())
                    .activityDate(recruit.getActivityDate() != null ? recruit.getActivityDate() : new Date())
                    .activityStart(recruit.getActivityStart() != null ? recruit.getActivityStart() : BigDecimal.ZERO)
                    .activityEnd(recruit.getActivityEnd() != null ? recruit.getActivityEnd() : BigDecimal.ZERO)
                    .deadline(recruit.getDeadline() != null ?
                            recruit.getDeadline().atZone(ZoneId.systemDefault()).toLocalDateTime()
                            : LocalDateTime.now())
                    .createdAt(recruit.getCreatedAt() != null ?
                            recruit.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                            : LocalDateTime.now())
                    .build();

            return ReadMyRecruitsResponseDto.RecruitDetail.builder()
                    .group(group)
                    .template(dtoTemplate)
                    .recruit(dtoRecruit)
                    .build();
        }).collect(Collectors.toList());

        return ReadMyRecruitsResponseDto.builder()
                .recruits(recruitDetails)
                .build();
    }


    @Override
    public DeleteRecruitsResponseDto deleteRecruits(DeleteRecruitsRequestDto deleteRecruitDto, AuthDto authDto) {

        checkOrgToken(authDto);

        List<Integer> recruitIds = deleteRecruitDto.getDeletedRecruits();

        int deletedCount = recruitRepository.markAsDeleted(recruitIds, authDto.getUserId());

        if (deletedCount == 0) {
            throw new NotFoundException("삭제할 수 있는 공고가 없습니다. 이미 삭제되었거나 권한이 없습니다.");
        }

        return DeleteRecruitsResponseDto.builder()
                .message("공고 삭제 완료")
                .deletedRecruits(recruitIds)
                .build();
    }


    @Override
    public UpdateRecruitsResponseDto updateRecruit(Integer recruitId, UpdateRecruitsRequestDto requestDto, AuthDto authDto) {

        checkOrgToken(authDto);

        Instant deadlineInstant = requestDto.getDeadline() != null
                ? requestDto.getDeadline().atZone(ZoneId.systemDefault()).toInstant()
                : Instant.now(); // null일 경우 현재 시간 설정

        Date activityDate = requestDto.getActivityDate() != null
                ? new java.sql.Date(requestDto.getActivityDate().getTime())
                : new java.sql.Date(System.currentTimeMillis());

        recruitRepository.updateRecruit(
                recruitId,
                deadlineInstant,
                activityDate,
                requestDto.getActivityStart() != null ? requestDto.getActivityStart() : BigDecimal.valueOf(10.00),
                requestDto.getActivityEnd() != null ? requestDto.getActivityEnd() : BigDecimal.valueOf(12.00),
                requestDto.getMaxVolunteer()
        );

        return UpdateRecruitsResponseDto.builder()
                .message("공고 수정 완료")
                .recruitId(recruitId)
                .build();

    }


    @Override
    public CloseRecruitResponseDto closeRecruit(CloseRecruitRequestDto closeRecruitDto, AuthDto authDto) {

        checkOrgToken(authDto);

        Integer recruitId = closeRecruitDto.getRecruitId();

        Optional<Recruit> optionalRecruit = recruitRepository.findById(recruitId);

        if (optionalRecruit.isEmpty()) {
            throw new NotFoundException("해당 공고를 찾을 수 없습니다.");
        }

        Recruit recruit = optionalRecruit.get();

        Integer recruitOrgId = recruit.getTemplate().getOrg().getId();
        Integer userOrgId = organizationRepository.findByUserId(authDto.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 사용자의 기관 정보를 찾을 수 없습니다."))
                .getId();

        if (!recruitOrgId.equals(userOrgId)) {
            throw new UnauthorizedException("해당 공고를 마감할 권한이 없습니다.");
        }

        recruitRepository.closeRecruit(recruitId);

        return CloseRecruitResponseDto.builder()
                .message("공고 마감 완료")
                .recruitId(recruitId)
                .build();
    }


    @Override
    public UpdateGroupResponseDto updateGroup(UpdateGroupRequestDto updateGroupDto, AuthDto authDto) {

        checkOrgToken(authDto);

        Organization userOrg = organizationRepository.findByUserId(authDto.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 사용자의 기관 정보를 찾을 수 없습니다."));
        TemplateGroup templateGroup = templateGroupRepository.findById(updateGroupDto.getGroupId())
                .orElseThrow(() -> new NotFoundException("해당 그룹을 찾을 수 없습니다."));

        if (!templateGroup.getOrg().getId().equals(userOrg.getId())) {
            throw new UnauthorizedException("이 그룹을 수정할 권한이 없습니다.");
        }

        templateGroupRepository.updateGroup(updateGroupDto.getGroupId(), userOrg.getId(), updateGroupDto.getGroupName());

        return UpdateGroupResponseDto.builder()
                .message("수정 성공")
                .groupId(updateGroupDto.getGroupId())
                .orgId(userOrg.getId())
                .build();
    }



    @Override
    public UpdateTemplateResponse updateTemplate(UpdateTemplateRequestDto updateTemplateDto, AuthDto authDto) {

        Organization org = organizationRepository.findById(updateTemplateDto.getOrgId())
                .orElseThrow(() -> new IllegalArgumentException("해당 기관 없음"));

        templateRepository.updateTemplate(updateTemplateDto.getTemplateId(), org, updateTemplateDto.getTitle(),
                updateTemplateDto.getDescription(), updateTemplateDto.getActivityLocation(),
                updateTemplateDto.getContactName(), updateTemplateDto.getContactPhone());

        return UpdateTemplateResponse.builder()
                .message("템플릿 수정 성공")
                .templateId(updateTemplateDto.getTemplateId())
                .orgId(updateTemplateDto.getOrgId())
                .build();

    }

    @Override
    public DeleteTemplatesResponseDto deleteTemplates(DeleteTemplatesRequestDto deleteTemplatesDto, AuthDto authDto) {

        List<Integer> deleteTemplateIds = deleteTemplatesDto.getDeletedTemplates();

        templateRepository.deleteTemplates(deleteTemplateIds);

        return DeleteTemplatesResponseDto.builder()
                .message("템플릿 삭제 성공")
                .deletedTemplates(deleteTemplateIds)  // 삭제된 템플릿 ID 리스트 전달
                .build();

    }

    @Override
    public DeleteGroupResponseDto deleteGroup(DeleteGroupDto deleteGroupDto, AuthDto authDto) {

        Integer groupId = deleteGroupDto.getGroupId();
        Integer orgId = deleteGroupDto.getOrgId();

        templateGroupRepository.deleteGroupByIdAndOrg(groupId, orgId);

        return DeleteGroupResponseDto.builder()
                .message("삭제 성공")
                .groupId(groupId)
                .orgId(orgId)
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public ReadTemplatesResponseDto readTemplates(Integer cursor, Integer limit, AuthDto authDto) {

        Pageable pageable = PageRequest.of(cursor, limit);
        List<TemplateGroup> groups = templateGroupRepository.findGroups(pageable);

        List<ReadTemplatesResponseDto.GroupDto> groupDtos = groups.stream()
                .map(group -> ReadTemplatesResponseDto.GroupDto.builder()
                        .groupId(group.getId())
                        .groupName(group.getGroupName())
                        .templates(
                                templateRepository.findTemplatesByGroupId(group.getId()).stream()
                                        .map(template -> {
                                            // 모든 이미지 프리사인드url 가져오기 (널값 제외)
                                            List<String> imageUrls = imageService.getImageUrls(template.getId(), true);

                                            return ReadTemplatesResponseDto.TemplateDto.builder()
                                                    .templateId(template.getId())
                                                    .orgId(template.getOrg().getId())
                                                    .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                                                    .title(template.getTitle())
                                                    .activityLocation(template.getActivityLocation())
                                                    .status(template.getStatus())
                                                    .images(imageUrls)
                                                    .contactName(template.getContactName())
                                                    .contactPhone(template.getContactPhone())
                                                    .description(template.getDescription())
                                                    .createdAt(template.getCreatedAt() != null
                                                            ? LocalDateTime.ofInstant(template.getCreatedAt(), ZoneId.systemDefault())
                                                            : LocalDateTime.now())
                                                    .build();
                                        }).collect(Collectors.toList())
                        )
                        .build()
                ).collect(Collectors.toList());

        return ReadTemplatesResponseDto.builder()
                .groups(groupDtos)
                .build();
    }

    @Override
    public CreateTemplateResponseDto createTemplate(CreateTemplateRequestDto createTemplateDto, AuthDto authDto) {

        checkOrgToken(authDto);

        if (createTemplateDto.getImageCount() != null && createTemplateDto.getImageCount() > 10) {
            throw new IllegalArgumentException("최대 개수를 초과했습니다. 최대 " + 10 + "개까지 업로드할 수 있습니다.");
        }

        Organization organization = organizationRepository.findByUserId(authDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 기관 정보 없음"));

        Template savedTemplate = templateRepository.save(Template.builder()
                .group(templateGroupRepository.findById(createTemplateDto.getGroupId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 그룹 없음")))
                .org(organization)
                .category(categoryRepository.findById(createTemplateDto.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 카테고리 없음")))
                .title(createTemplateDto.getTitle())
                .activityLocation(createTemplateDto.getActivityLocation())
                .status(createTemplateDto.getStatus())
                .contactName(createTemplateDto.getContactName())
                .contactPhone(createTemplateDto.getContactPhone())
                .description(createTemplateDto.getDescription())
                .isDeleted(false)
                .createdAt(Instant.now())
                .build());

        imageService.initializeReviewImages(savedTemplate.getId(), true);

        if (createTemplateDto.getImages() != null && !createTemplateDto.getImages().isEmpty()) {
            imageService.updateReviewImages(savedTemplate.getId(), createTemplateDto.getImages());
        }

        imageService.updateThumbnailImage(savedTemplate.getId(), true);

        List<String> imageUrls = imageService.getImageUrls(savedTemplate.getId(), true);

        return CreateTemplateResponseDto.builder()
                .message("템플릿 생성 성공")
                .templateId(savedTemplate.getId())
                .imageUrl(imageUrls.stream().findFirst().orElse(null)) // 썸네일 이미지 url
                .images(imageUrls)
                .build();
    }



    @Override
    public CreateGroupResponseDto createGroup(CreateGroupRequestDto createGroupDto, AuthDto authDto) {

        Organization org = organizationRepository.findById(createGroupDto.getOrgId())
                .orElseThrow(() -> new IllegalArgumentException("기관 없음"));

        TemplateGroup newGroup = TemplateGroup.builder()
                .org(org)
                .groupName(createGroupDto.getGroupName() != null ? createGroupDto.getGroupName() : "봉사")
                .isDeleted(false)
                .build();

        TemplateGroup savedGroup = templateGroupRepository.save(newGroup);

        return CreateGroupResponseDto.builder()
                .message("그룹 생성 성공")
                .groupId(savedGroup.getId())
                .build();

    }

    @Override
    public CreateRecruitResponseDto createRecruit(CreateRecruitRequestDto createRecruitDto, AuthDto authDto) {

        Instant deadlineInstant = createRecruitDto.getDeadline() != null
                ? createRecruitDto.getDeadline().atZone(ZoneId.systemDefault()).toInstant()
                : Instant.now();

        Template template = templateRepository.findById(createRecruitDto.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("해당 템플릿이 존재하지 않습니다."));

        Recruit recruit = Recruit.builder()
                .template(template)
                .deadline(deadlineInstant)
                .activityDate(createRecruitDto.getActivityDate() != null ? createRecruitDto.getActivityDate() : new Date())
                .activityStart(createRecruitDto.getActivityStart() != null ? createRecruitDto.getActivityStart() : BigDecimal.ZERO)
                .activityEnd(createRecruitDto.getActivityEnd() != null ? createRecruitDto.getActivityEnd() : BigDecimal.ZERO)
                .maxVolunteer(createRecruitDto.getMaxVolunteer() != null ? createRecruitDto.getMaxVolunteer() : 0)
                .status("RECRUITING")
                .isDeleted(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        recruit = recruitRepository.save(recruit);

        return CreateRecruitResponseDto.builder()
                .message("공고 생성 완료")
                .recruitId(recruit.getId())
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public ReadRecruitResponseDto readRecruit(Integer recruitId, AuthDto authDto) {

        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new RuntimeException("해당 공고가 없습니다"));

        LocalDateTime deadlineLocalDateTime = recruit.getDeadline() != null
                ? LocalDateTime.ofInstant(recruit.getDeadline(), ZoneId.systemDefault())
                : null;

        LocalDateTime updatedAtLocalDateTime = recruit.getUpdatedAt() != null
                ? LocalDateTime.ofInstant(recruit.getUpdatedAt(), ZoneId.systemDefault())
                : LocalDateTime.now();

        LocalDateTime createdAtLocalDateTime = recruit.getCreatedAt() != null
                ? LocalDateTime.ofInstant(recruit.getCreatedAt(), ZoneId.systemDefault())
                : LocalDateTime.now();

        Date activityDate = recruit.getActivityDate() != null ? recruit.getActivityDate() : new Date();

        ReadRecruitResponseDto.Recruit recruitDto = ReadRecruitResponseDto.Recruit.builder()
                .recruitId(recruit.getId())
                .deadline(deadlineLocalDateTime)
                .activityDate(activityDate)
                .activityStart(recruit.getActivityStart() != null ? recruit.getActivityStart() : BigDecimal.ZERO)
                .activityEnd(recruit.getActivityEnd() != null ? recruit.getActivityEnd() : BigDecimal.ZERO)
                .maxVolunteer(recruit.getMaxVolunteer())
                .participateVolCount(recruit.getParticipateVolCount())
                .status(recruit.getStatus())
                .updatedAt(updatedAtLocalDateTime)
                .createdAt(createdAtLocalDateTime)
                .build();

        ReadRecruitResponseDto.Group groupDto = new ReadRecruitResponseDto.Group(
                recruit.getTemplate().getGroup().getId(),
                recruit.getTemplate().getGroup().getGroupName()
        );

        LocalDateTime templateCreatedAt = recruit.getTemplate().getCreatedAt() != null
                ? LocalDateTime.ofInstant(recruit.getTemplate().getCreatedAt(), ZoneId.systemDefault())
                : LocalDateTime.now();

        List<String> imageUrls = imageService.getImageUrls(recruit.getTemplate().getId(), true);

        ReadRecruitResponseDto.Template templateDto = ReadRecruitResponseDto.Template.builder()
                .templateId(recruit.getTemplate().getId())
                .categoryId(recruit.getTemplate().getCategory() != null ? recruit.getTemplate().getCategory().getId() : null)
                .title(recruit.getTemplate().getTitle())
                .activityLocation(recruit.getTemplate().getActivityLocation())
                .status(recruit.getTemplate().getStatus())
                .images(imageUrls)
                .contactName(recruit.getTemplate().getContactName())
                .contactPhone(recruit.getTemplate().getContactPhone())
                .description(recruit.getTemplate().getDescription())
                .createdAt(templateCreatedAt)
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

    @Override
    @Transactional(readOnly = true)
    public ReadApplicationsResponseDto readApplications(Integer recruitId, AuthDto authDto) {

        List<Application> applications = applicationRepository.findByRecruitIdWithUser(recruitId);

        List<ReadApplicationsResponseDto.ApplicationDetail> applicationDetails = applications.stream()
                .map(application -> ReadApplicationsResponseDto.ApplicationDetail.builder()
                        .user(ReadApplicationsResponseDto.User.builder()
                                .userId(application.getVolunteer().getUser().getId())
                                .email(application.getVolunteer().getUser().getEmail())
                                .name(application.getVolunteer().getUser().getName())
                                .profileImage(application.getVolunteer().getUser().getProfileImage())
                                .build())
                        .volunteer(ReadApplicationsResponseDto.Volunteer.builder()
                                .volunteerId(application.getVolunteer().getId())
                                .recommendedCount(application.getVolunteer().getRecommendedCount())
                                .totalVolunteerHours(
                                        application.getVolunteer().getUser().getTotalVolunteerHours() != null
                                                ? application.getVolunteer().getUser().getTotalVolunteerHours().intValue()
                                                : 0
                                )
                                .build())
                        .application(ReadApplicationsResponseDto.Application.builder()
                                .applicationId(application.getId())
                                .recruitId(application.getRecruit().getId())
                                .status(application.getStatus())
                                .createdAt(application.getCreatedAt() != null
                                        ? LocalDateTime.ofInstant(application.getCreatedAt(), ZoneId.systemDefault())
                                        : LocalDateTime.now())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return ReadApplicationsResponseDto.builder()
                .recruitId(recruitId)
                .applications(applicationDetails)
                .build();

    }

    @Override
    public UpdateApplicationsResponseDto updateStatuses(UpdateApplicationsRequestDto updateApplicationDto, AuthDto authDto) {

        Integer applicationId = updateApplicationDto.getApplicationId();
        Integer recruitId = updateApplicationDto.getRecruitId();
        Boolean accept = updateApplicationDto.getAccept();

        String status = accept ? "APPROVED" : "REJECTED";

        applicationRepository.updateApplicationStatus(applicationId, status);

        return UpdateApplicationsResponseDto.builder()
                .message("신청 상태 변경 완료")
                .application(UpdateApplicationsResponseDto.Application.builder()
                        .applicationId(applicationId)
                        .recruitId(recruitId)
                        .status(status)
                        .createdAt(LocalDateTime.now())
                        .build())
                .build();

    }

    @Override
    public List<EvaluateApplicationsResponseDto> evaluateApplicants(
            Integer recruitId,
            List<EvaluateApplicationsRequestDto> evaluateApplicationDtoList,
            AuthDto authDto) {

        return evaluateApplicationDtoList.stream().map(dto -> {
            Integer volunteerId = dto.getVolunteerId();
            String recommendationStatus = dto.getRecommendationStatus();

            if ("RECOMMEND".equalsIgnoreCase(recommendationStatus)) {
                volunteerRepository.incrementRecommendation(volunteerId);
            } else if ("NOTRECOMMEND".equalsIgnoreCase(recommendationStatus)) {
                volunteerRepository.incrementNotRecommendation(volunteerId);
            }

            return EvaluateApplicationsResponseDto.builder()
                    .volunteerId(volunteerId)
                    .recommendationStatus(recommendationStatus)
                    .build();
        }).collect(Collectors.toList());

    }

}
