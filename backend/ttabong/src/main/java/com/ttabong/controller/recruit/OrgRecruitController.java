package com.ttabong.controller.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;
import com.ttabong.service.recruit.OrgRecruitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/org")
@RequiredArgsConstructor
@Slf4j
public class OrgRecruitController {
    private final OrgRecruitService orgRecruitService;

    //1. 메인페이지 /api/org/templates/available?cursor={templateId}&limit={limit}
    @GetMapping("/templates/available")
    public ResponseEntity<ReadAvailableRecruitsResponseDto> readAvailableRecruits(
            @RequestParam(required = false, name = "templateId") Integer cursor,
            @RequestParam(defaultValue = "10", name = "limit") Integer limit) {

        ReadAvailableRecruitsResponseDto response = orgRecruitService.readAvailableRecruits(cursor, limit);

        return ResponseEntity.ok().body(response);
    }


    //2. 공고 _ 공고 전체 조회(그룹+템플릿+공고) /api/org/recruits?cursor={recruitId}&limit={limit}
    @GetMapping("/recruits")
    public ResponseEntity<ReadMyRecruitsResponseDto> readRecruits(
            @RequestParam(required = false, name = "recruitId") Integer cursor,
            @RequestParam(defaultValue = "10", name = "limit") Integer limit) {

        ReadMyRecruitsResponseDto response = orgRecruitService.readMyRecruits(cursor, limit);
        return ResponseEntity.ok(response);
    }

    //3. 공고 _ 공고 삭제 (여러개 선택 후 한 번에 삭제) /api/org/recruits/delete
    @PatchMapping("/recruits/delete")
    public ResponseEntity<DeleteRecruitsResponseDto> deleteRecruits(@RequestBody DeleteRecruitsRequestDto deleteRecruitDto) {

        DeleteRecruitsResponseDto response = orgRecruitService.deleteRecruits(deleteRecruitDto);
        return ResponseEntity.ok(response);
    }

    //4 b
    @PatchMapping("/recruits/{recruitId}")
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