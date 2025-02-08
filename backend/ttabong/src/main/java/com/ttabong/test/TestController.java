package com.ttabong.test;

import com.ttabong.test.dto.CreatePostRequestDto;
import com.ttabong.test.dto.CreatePostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/startPost/{userId}")
    public ResponseEntity<?> startPost(@PathVariable Integer userId) throws Exception {

        CreatePostResponseDto responseDto = testService.startPostCache(userId);
        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/endPost/{userId}")
    public ResponseEntity<?> endPost(@PathVariable Integer userId, @RequestBody CreatePostRequestDto createPostRequestDto) throws Exception {

        testService.endPost(createPostRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getImg/{imgId}")
    public ResponseEntity<?> getImg(@PathVariable String imgId) throws Exception {

        return ResponseEntity.ok().body(testService.getImgUrl(imgId));
    }
}
