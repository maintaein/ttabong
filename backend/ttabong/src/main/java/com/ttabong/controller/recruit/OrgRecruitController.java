package com.ttabong.controller.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.ReadMyRecruitsResponseDto;
import com.ttabong.dto.recruit.responseDto.org.ReadAvailableRecruitsResponseDto;
import com.ttabong.dto.recruit.responseDto.org.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("org")
public class OrgRecruitController {

    //1
    @GetMapping("/templates/available")
    public ResponseEntity<ReadAvailableRecruitsResponseDto> readAvailableRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().body(new ReadAvailableRecruitsResponseDto());
    }

    //2
    @GetMapping("/recruits")
    public ResponseEntity<ReadMyRecruitsResponseDto> readRecruits(@RequestParam(required = true) int cursor, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok().body(new ReadMyRecruitsResponseDto());
    }

    //3 b
    @PatchMapping("recruits/delete")
    public ResponseEntity<DeleteRecruitsResponseDto> deleteRecruits(@RequestBody DeleteRecruitsRequestDto deleteRecruitDto) {
        return ResponseEntity.ok().body(new DeleteRecruitsResponseDto());
    }

    //4 b
    @PatchMapping("recruits/{recruitId}")
    public ResponseEntity<UpdateRecruitsResponseDto> updateRecruit(@PathVariable int recruitId, @RequestBody UpdateRecruitsRequestDto updateRecruitDto) {
        return ResponseEntity.ok().body(new UpdateRecruitsResponseDto());
    }

    //5 b
    @PatchMapping("recruits/close")
    public ResponseEntity<CloseRecruitResponseDto> closeRecruit(@RequestBody CloseRecruitRequestDto closeRecruitDto) {
        return ResponseEntity.ok().body(new CloseRecruitResponseDto());
    }

    //7 6
    @PatchMapping("groups")
    public ResponseEntity<UpdateGroupResponseDto> updateGroup(@RequestBody UpdateGroupRequestDto updateGroupDto) {
        return ResponseEntity.ok().body(new UpdateGroupResponseDto());
    }

    //7 b
    @PatchMapping("templates")
    public ResponseEntity<UpdateTemplateResponse> updateTemplate(@RequestBody UpdateTemplateRequestDto updateTemplateDto) {
        return ResponseEntity.ok().body(new UpdateTemplateResponse());
    }

    //8 b
    @PatchMapping("templates/delete")
    public ResponseEntity<DeleteTemplatesResponseDto> deleteTemplates(@RequestBody DeleteTemplatesRequestDto deleteTemplatesDto) {
        return ResponseEntity.ok().body(new DeleteTemplatesResponseDto());
    }

    //9 b
    @PatchMapping("groups/delete")
    public ResponseEntity<DeleteGroupResponseDto> deleteGroup(@RequestBody DeleteGroupDto deleteGroupDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().body(new DeleteGroupResponseDto());
    }

    //10
    @GetMapping("templates")
    public ResponseEntity<ReadTemplatesResponseDto> readTemplates(@RequestParam(defaultValue = "0") int cursor, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok().body(new ReadTemplatesResponseDto());
    }

    //11 b
    @PostMapping("templates")
    public ResponseEntity<CreateTemplateResponseDto> createTemplate(@RequestBody CreateTemplateRequestDto createTemplateDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().body(new CreateTemplateResponseDto());
    }

    //12 b
    @PostMapping("groups")
    public ResponseEntity<CreateGroupResponseDto> createGroup(@RequestBody CreateGroupRequestDto createGroupDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().body(new CreateGroupResponseDto());
    }

    //13 b
    @PostMapping("recruits")
    public ResponseEntity<CreateRecruitResponseDto> createRecruit(@RequestBody CreateRecruitRequestDto createRecruitDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().body(new CreateRecruitResponseDto());
    }

    //14
    @GetMapping("recruits/{recruitId}")
    public ResponseEntity<ReadRecruitResponseDto> readRecruit() {//@PathVariable int recruitId) {
        return ResponseEntity.ok().body(new ReadRecruitResponseDto());
    }

    //15
    @GetMapping("recruits/{recruitId}/applications")
    public ResponseEntity<readApplicationsResponseDto> readApplications(@PathVariable int recruitId) {
        return ResponseEntity.ok().body(new readApplicationsResponseDto());
    }

    //16 b
    @PatchMapping("applications/status")
    public ResponseEntity<UpdateApplicationsResponseDto> updateStatuses(@RequestBody UpdateApplicationsRequestDto updateApplicationDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().body(new UpdateApplicationsResponseDto());
    }

    //17 b
    @PatchMapping("recruits/{recruitId}/applications/evaluate")
    public ResponseEntity<List<EvaluateApplicationsResponseDto>> evaluateApplicants(@PathVariable int recruitId, EvaluateApplicationsRequestDto evaluateApplicationDto) {//}, @RequestBody Object object) {
        return ResponseEntity.ok().body(List.of(new EvaluateApplicationsResponseDto()));
    }
}