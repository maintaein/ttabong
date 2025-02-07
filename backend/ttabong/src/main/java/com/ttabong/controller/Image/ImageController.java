package com.ttabong.controller.Image;

import com.ttabong.service.image.ImageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.InputStream;

@RestController
@RequestMapping("/api/minio")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    // ✅ 1. 파일 업로드 Pre-signed URL 생성 API
    @GetMapping("/presigned/upload/{fileName}")
    public ResponseEntity<String> getPresignedUploadUrl(@PathVariable String fileName) {
        String url = imageService.generatePresignedUploadUrl(fileName);
        return ResponseEntity.ok(url);
    }

    // ✅ 2. 파일 다운로드 Pre-signed URL 생성 API
    @GetMapping("/presigned/download/{fileName}")
    public ResponseEntity<String> getPresignedDownloadUrl(@PathVariable String fileName) {
        String url = imageService.generatePresignedDownloadUrl(fileName);
        return ResponseEntity.ok(url);
    }
    // ✅ 1. 버킷 존재 여부 확인 API
    @GetMapping("/bucket-exists")
    public ResponseEntity<String> checkBucket() {
        boolean exists = imageService.bucketExists();
        return ResponseEntity.ok("✅ 버킷 존재 여부: " + exists);
    }

    // ✅ 2. 파일 업로드 API
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String response = imageService.uploadFile(file);
        return ResponseEntity.ok(response);
    }

    // ✅ 3. 파일 다운로드 API
    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        InputStream fileStream = imageService.downloadFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(fileStream));
    }

    // ✅ 4. 파일 삭제 API
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        String response = imageService.deleteFile(fileName);
        return ResponseEntity.ok(response);
    }

    // ✅ 5. 파일 접근 URL 생성 API
    @GetMapping("/url/{fileName}")
    public ResponseEntity<String> getFileUrl(@PathVariable String fileName) {
        String url = imageService.generateFileUrl(fileName);
        return ResponseEntity.ok("✅ 파일 URL: " + url);
    }
}
