package com.ttabong.controller.recruit;

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
    public ResponseEntity<?> deleteRecruits() {
        return ResponseEntity.ok().build();
    }

    //4 b
    @PatchMapping("recruits/{recruitId}")
    public ResponseEntity<?> updateRecruit(@PathVariable int recruitId) {
        return ResponseEntity.ok().build();
    }

    //5 b
    @PatchMapping("recruits/close")
    public ResponseEntity<?> closeRecruit() {
        return ResponseEntity.ok().build();
    }

    //7 6
    @PatchMapping("groups")
    public ResponseEntity<?> updateGroup() {
        return ResponseEntity.ok().build();
    }

    //7 b
    @PatchMapping("templates")
    public ResponseEntity<?> updateTemplate() {
        return ResponseEntity.ok().build();
    }

    //8 b
    @PatchMapping("templates/delete")
    public ResponseEntity<?> deleteTemplates() {
        return ResponseEntity.ok().build();
    }

    //9 b
    @PatchMapping("groups/delete")
    public ResponseEntity<?> deleteGroup() {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //10
    @GetMapping("templates")
    public ResponseEntity<?> readTemplates(@RequestParam(defaultValue = "0") int cursor, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok().build();
    }

    //11 b
    @PostMapping("templates")
    public ResponseEntity<?> createTemplate() {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //12 b
    @PostMapping("groups")
    public ResponseEntity<?> createGroup() {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //13 b
    @PostMapping("recruits")
    public ResponseEntity<?> createRecruit() {//@RequestBody Object object) {
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
    public ResponseEntity<?> updateStatuses() {//@RequestBody Object object) {
        return ResponseEntity.ok().build();
    }

    //17 b
    @PatchMapping("recruits/{recruitId}/applications/evaluate")
    public ResponseEntity<?> evaluateApplicants(@PathVariable int recruitId) {//}, @RequestBody Object object) {
        return ResponseEntity.ok().build();
    }
}