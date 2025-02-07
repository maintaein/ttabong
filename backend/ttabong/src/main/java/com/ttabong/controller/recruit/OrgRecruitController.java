package com.ttabong.controller.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("org")
public class OrgRecruitController {

    //1
    @GetMapping("/templates/available")
    public ResponseEntity<ReadAvailableRecruitsResponseDto> readAvailableRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        ReadAvailableRecruitsResponseDto.Template template = ReadAvailableRecruitsResponseDto.Template.builder()
                .templateId(1)
                .categoryId(3)
                .title("환경 정화 봉사")
                .activityLocation("서울특별시 종로구")
                .status("ALL")
                .imageId("https://example.com/template_image.jpg")
                .contactName("김봉사")
                .contactPhone("010-1234-5678")
                .description("서울 시내 공원에서 환경 정화 활동을 진행합니다.")
                .createdAt(LocalDateTime.now())
                .build();

        ReadAvailableRecruitsResponseDto.Group group = ReadAvailableRecruitsResponseDto.Group.builder()
                .groupId(10)
                .groupName("환경 보호 단체")
                .build();

        ReadAvailableRecruitsResponseDto.Recruit recruit = ReadAvailableRecruitsResponseDto.Recruit.builder()
                .recruitId(100)
                .deadline(LocalDateTime.now().plusDays(10))
                .activityDate(LocalDateTime.now().toLocalDate())
                .activityStart(1000)
                .activityEnd(1400)
                .maxVolunteer(20)
                .participateVolCount(5)
                .status("모집중")
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        ReadAvailableRecruitsResponseDto.TemplateDetail templateDetail = ReadAvailableRecruitsResponseDto.TemplateDetail.builder()
                .template(template)
                .group(group)
                .recruits(Collections.singletonList(recruit))
                .build();

        ReadAvailableRecruitsResponseDto responseDto = ReadAvailableRecruitsResponseDto.builder()
                .templates(Collections.singletonList(templateDetail))
                .build();

        return ResponseEntity.ok().body(responseDto);
    }


    //2
    @GetMapping("/recruits")
    public ResponseEntity<ReadMyRecruitsResponseDto> readRecruits(@RequestParam(required = true) int cursor, @RequestParam(defaultValue = "10") int limit) {
        ReadMyRecruitsResponseDto.Group group = ReadMyRecruitsResponseDto.Group.builder()
                .groupId(1)
                .groupName("청소년 봉사").build();

        ReadMyRecruitsResponseDto.Template template = ReadMyRecruitsResponseDto.Template.builder()
                .templateId(10)
                .title("환경 정화 봉사").build();

        ReadMyRecruitsResponseDto.Recruit recruit = ReadMyRecruitsResponseDto.Recruit.builder()
                .recruitId(100)
                .status("모집중")
                .maxVolunteer(20)
                .participateVolCount(10)
                .activityDate(LocalDate.now())
                .activityStart(10.00)
                .activityEnd(14.00)
                .deadline(LocalDateTime.now().plusDays(10))
                .createdAt(LocalDateTime.now()).build();

        ReadMyRecruitsResponseDto.RecruitDetail recruitDetail = ReadMyRecruitsResponseDto.RecruitDetail.builder()
                .group(group)
                .template(template)
                .recruit(recruit).build();

        ReadMyRecruitsResponseDto responseDto = ReadMyRecruitsResponseDto.builder()
                .recruits(Collections.singletonList(recruitDetail)).build();

        return ResponseEntity.ok().body(responseDto);
    }

    //3 b
    @PatchMapping("recruits/delete")
    public ResponseEntity<DeleteRecruitsResponseDto> deleteRecruits(@RequestBody DeleteRecruitsRequestDto deleteRecruitDto) {
        List<Integer> deletedRecruits = deleteRecruitDto.getDeletedRecruits();

        DeleteRecruitsResponseDto responseDto = DeleteRecruitsResponseDto.builder()
                .message("삭제 성공")
                .deletedRecruits(deletedRecruits)
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //4 b
    @PatchMapping("recruits/{recruitId}")
    public ResponseEntity<UpdateRecruitsResponseDto> updateRecruit(@PathVariable int recruitId, @RequestBody UpdateRecruitsRequestDto updateRecruitDto) {
        UpdateRecruitsResponseDto responseDto = UpdateRecruitsResponseDto.builder()
                .message("수정 성공")
                .recruitId(recruitId)
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //5 b
    @PatchMapping("recruits/close")
    public ResponseEntity<CloseRecruitResponseDto> closeRecruit(@RequestBody CloseRecruitRequestDto closeRecruitDto) {
        CloseRecruitResponseDto responseDto = CloseRecruitResponseDto.builder()
                .message("마감 완료")
                .recruitId(closeRecruitDto.getRecruitId())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //7 6
    @PatchMapping("/groups")
    public ResponseEntity<UpdateGroupResponseDto> updateGroup(@RequestBody UpdateGroupRequestDto updateGroupDto) {

        UpdateGroupResponseDto responseDto = UpdateGroupResponseDto.builder()
                .message("수정 성공")
                .groupId(updateGroupDto.getGroupId())
                .orgId(updateGroupDto.getOrgId())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //7 b
    @PatchMapping("templates")
    public ResponseEntity<UpdateTemplateResponse> updateTemplate(@RequestBody UpdateTemplateRequestDto updateTemplateDto) {
        UpdateTemplateResponse responseDto = UpdateTemplateResponse.builder()
                .message("수정 성공")
                .templateId(updateTemplateDto.getTemplateId())
                .orgId(updateTemplateDto.getOrgId())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //8 b
    @PatchMapping("/templates/delete")
    public ResponseEntity<DeleteTemplatesResponseDto> deleteTemplates(@RequestBody DeleteTemplatesRequestDto deleteTemplatesDto) {

        List<Integer> deletedTemplates = deleteTemplatesDto.getDeleteTemplateIds();

        DeleteTemplatesResponseDto responseDto = DeleteTemplatesResponseDto.builder()
                .message("삭제 성공")
                .deletedTemplates(deletedTemplates)
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //9 b
    @PatchMapping("/groups/delete")
    public ResponseEntity<DeleteGroupResponseDto> deleteGroup(@RequestBody DeleteGroupDto deleteGroupDto) {

        DeleteGroupResponseDto responseDto = DeleteGroupResponseDto.builder()
                .message("삭제 성공")
                .groupId(deleteGroupDto.getGroupId())
                .orgId(deleteGroupDto.getOrgId())
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //10
    @GetMapping("templates")
    public ResponseEntity<ReadTemplatesResponseDto> readTemplates(@RequestParam(defaultValue = "0") int cursor, @RequestParam(defaultValue = "10") int limit) {
        // ✅ 부모 객체 먼저 생성
        ReadTemplatesResponseDto responseDto = new ReadTemplatesResponseDto();

        // ✅ 부모 객체를 통해 내부 클래스 인스턴스 생성
        ReadTemplatesResponseDto.GroupDto groupDto = responseDto.new GroupDto();
        groupDto.setGroupId(1);
        groupDto.setGroupName("청소년 봉사");

        ReadTemplatesResponseDto.TemplateDto templateDto = responseDto.new TemplateDto();
        templateDto.setTemplateId(10);
        templateDto.setOrgId(5);
        templateDto.setCategoryId(3);
        templateDto.setTitle("환경 정화 봉사");
        templateDto.setActivityLocation("서울특별시 종로구");
        templateDto.setStatus("ALL");
        templateDto.setImageId("https://example.com/image.jpg");
        templateDto.setContactName("김봉사");
        templateDto.setContactPhone("010-1234-5678");
        templateDto.setDescription("환경 보호 봉사활동");
        templateDto.setCreatedAt(LocalDateTime.now());

        // ✅ 리스트에 추가
        groupDto.setTemplates(List.of(templateDto));
        responseDto.setGroups(List.of(groupDto));
        return ResponseEntity.ok().body(responseDto);
    }

    //11 b
    @PostMapping("/templates")
    public ResponseEntity<CreateTemplateResponseDto> createTemplate(@RequestBody CreateTemplateRequestDto createTemplateDto) {

        CreateTemplateResponseDto responseDto = CreateTemplateResponseDto.builder()
                .message("템플릿 생성 성공")
                .templateId(1) // 임시값, 실제 생성된 templateId를 반환해야 함
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //12 b
    @PostMapping("/groups")
    public ResponseEntity<CreateGroupResponseDto> createGroup(@RequestBody CreateGroupRequestDto createGroupDto) {

        CreateGroupResponseDto responseDto = CreateGroupResponseDto.builder()
                .message("그룹 생성 성공")
                .groupId(1) // 임시값, 실제 생성된 groupId를 반환해야 함
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //13 b
    @PostMapping("/recruits")
    public ResponseEntity<CreateRecruitResponseDto> createRecruit(@RequestBody CreateRecruitRequestDto createRecruitDto) {

        CreateRecruitResponseDto responseDto = CreateRecruitResponseDto.builder()
                .message("공고 생성 완료")
                .recruitId(1) // 임시값, 실제 생성된 recruitId를 반환해야 함
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //14
    @GetMapping("/recruits/{recruitId}")
    public ResponseEntity<ReadRecruitResponseDto> readRecruit(@PathVariable int recruitId) {

        ReadRecruitResponseDto responseDto = new ReadRecruitResponseDto();

        return ResponseEntity.ok().body(responseDto);
    }

    //15
    @GetMapping("/recruits/{recruitId}/applications")
    public ResponseEntity<ReadApplicationsResponseDto> readApplications(@PathVariable int recruitId) {

        ReadApplicationsResponseDto responseDto = new ReadApplicationsResponseDto();

        return ResponseEntity.ok().body(responseDto);
    }

    //16 b
    @PatchMapping("/applications/status")
    public ResponseEntity<UpdateApplicationsResponseDto> updateStatuses(@RequestBody UpdateApplicationsRequestDto updateApplicationDto) {

        UpdateApplicationsResponseDto.Application application = new UpdateApplicationsResponseDto.Application();
        application.setApplicationId(updateApplicationDto.getApplicationId());
        application.setRecruitId(updateApplicationDto.getRecruitId());
        application.setStatus(updateApplicationDto.getAccept() ? "ACCEPTED" : "REJECTED");
        application.setCreatedAt(java.time.LocalDateTime.now());

        UpdateApplicationsResponseDto responseDto = UpdateApplicationsResponseDto.builder()
                .message("신청 상태 변경 완료")
                .application(application)
                .build();

        return ResponseEntity.ok().body(responseDto);
    }

    //17 b
    @PatchMapping("/recruits/{recruitId}/applications/evaluate")
    public ResponseEntity<List<EvaluateApplicationsResponseDto>> evaluateApplicants(@PathVariable int recruitId, @RequestBody List<EvaluateApplicationsRequestDto> evaluateApplicationDtoList) {

        List<EvaluateApplicationsResponseDto> responseList = evaluateApplicationDtoList.stream()
                .map(dto -> EvaluateApplicationsResponseDto.builder()
                        .volunteerId(dto.getVolunteerId())
                        .recommendationStatus(dto.getRecommendationStatus())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(responseList);
    }
}