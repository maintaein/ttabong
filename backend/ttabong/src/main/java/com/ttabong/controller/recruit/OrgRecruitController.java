package com.ttabong.controller.recruit;

import com.ttabong.dto.recruit.requestDto.org.*;
import com.ttabong.dto.recruit.responseDto.org.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("org")
public class OrgRecruitController {

    //1
    @GetMapping("/templates/available")
    public ResponseEntity<?> readAvailableRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().build();
    }

    //2
    @GetMapping("/recruits")
    public ResponseEntity<?> readRecruits(@RequestParam(required = true) int cursor, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok().build();
    }

    //3 b
    @PatchMapping("recruits/delete")
    public ResponseEntity<DeleteRecruitsResponse> deleteRecruits(@RequestBody DeleteRecruitsRequestDto deleteRecruitDto) {
        return ResponseEntity.ok().build();
    }

    //4 b
    @PatchMapping("recruits/{recruitId}")
    public ResponseEntity<UpdateRecruitsResponse> updateRecruit(@PathVariable int recruitId, @RequestBody UpdateRecruitsRequestDto updateRecruitDto) {
        return ResponseEntity.ok().build();
    }

    //5 b
    @PatchMapping("recruits/close")
    public ResponseEntity<CloseRecruitResponse> closeRecruit(@RequestBody CloseRecruitRequestDto closeRecruitDto) {
        return ResponseEntity.ok().build();
    }

    //7 6
    @PatchMapping("groups")
    public ResponseEntity<UpdateGroupResponse> updateGroup(@RequestBody UpdateGroupRequestDto updateGroupDto) {
        return ResponseEntity.ok().build();
    }

    //7 b
    @PatchMapping("templates")
    public ResponseEntity<UpdateTemplateResponse> updateTemplate(@RequestBody UpdateTemplateRequestDto updateTemplateDto) {
        return ResponseEntity.ok().build();
    }

    //8 b
    @PatchMapping("templates/delete")
    public ResponseEntity<DeleteTemplatesResponse> deleteTemplates(@RequestBody DeleteTemplatesRequestDto deleteTemplatesDto) {
        return ResponseEntity.ok().build();
    }

    //9 b
    @PatchMapping("groups/delete")
    public ResponseEntity<DeleteGroupResponse> deleteGroup(@RequestBody DeleteGroupDto deleteGroupDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //10
    @GetMapping("templates")
    public ResponseEntity<?> readTemplates(@RequestParam(defaultValue = "0") int cursor, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok().build();
    }

    //11 b
    @PostMapping("templates")
    public ResponseEntity<CreateTemplateResponse> createTemplate(@RequestBody CreateTemplateRequestDto createTemplateDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //12 b
    @PostMapping("groups")
    public ResponseEntity<CreateGroupResponse> createGroup(@RequestBody CreateGroupRequestDto createGroupDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //13 b
    @PostMapping("recruits")
    public ResponseEntity<CreateRecruitResponse> createRecruit(@RequestBody CreateRecruitRequestDto createRecruitDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //14
    @GetMapping("recruits/{recruitId}")
    public ResponseEntity<?> readRecruit() {//@PathVariable int recruitId) {
        return ResponseEntity.ok().build();
    }

    //15
    @GetMapping("recruits/{recruitId}/applications")
    public ResponseEntity<?> readApplications(@PathVariable int recruitId) {
        return ResponseEntity.ok().build();
    }

    //16 b
    @PatchMapping("applications/status")
    public ResponseEntity<UpdateApplicationsResponse> updateStatuses(@RequestBody UpdateApplicationsRequestDto updateApplicationDto) {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //17 b
    @PatchMapping("recruits/{recruitId}/applications/evaluate")
    public ResponseEntity<EvaluateApplicationsResponse> evaluateApplicants(@PathVariable int recruitId, EvaluateApplicationsRequestDto evaluateApplicationDto) {//}, @RequestBody Object object) {
        return ResponseEntity.ok().build();
    }
}