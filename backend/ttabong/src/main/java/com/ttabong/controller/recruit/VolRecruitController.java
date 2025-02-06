package com.ttabong.controller.recruit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("vol")
public class VolRecruitController {

    @GetMapping("templates")
    public ResponseEntity<?> listRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().build();
    }
    @GetMapping("templates/{templateId}")
    public ResponseEntity<?> RecruitsDetail(@PathVariable String templateId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("applications")
    public ResponseEntity<?> applyRecruit() {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("applications/{applicationsId}")
    public ResponseEntity<?> cancelRecruit(@PathVariable String applicationsId) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("applications/recruits")
    public ResponseEntity<?> myApplications(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("recruits")
    public ResponseEntity<?> myApplicationsDetail(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().build();

    }

    @GetMapping("volunteer-reactions/like")
    public ResponseEntity<?> myLikesOnRecruits(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("volunteer_reactions")
    public ResponseEntity<?> likeOnRecruit() {
        return ResponseEntity.ok().build();
    }
    @PatchMapping("volunteer_reactions/cancel")
    public ResponseEntity<?> deleteRecruitfromLike(@RequestParam Integer cursor, @RequestParam Integer limit) {
        return ResponseEntity.ok().build();
    }
}