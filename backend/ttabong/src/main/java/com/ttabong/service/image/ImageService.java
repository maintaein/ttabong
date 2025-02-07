package com.ttabong.service.image;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class ImageService {

    private final MinioClient minioClient;
    private final String bucketName = "ttabong"; // MinIO 버킷명

    public ImageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // ✅ 1. 버킷 존재 여부 확인
    public boolean bucketExists() {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
        } catch (Exception e) {
            throw new RuntimeException("MinIO 버킷 확인 오류: " + e.getMessage());
        }
    }

    // ✅ 2. 파일 업로드
    public String uploadFile(MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return "✅ 파일 업로드 성공: " + file.getOriginalFilename();
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 오류: " + e.getMessage());
        }
    }

    // ✅ 3. 파일 다운로드
    public InputStream downloadFile(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 오류: " + e.getMessage());
        }
    }

    // ✅ 4. 파일 삭제
    public String deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return "✅ 파일 삭제 성공: " + fileName;
        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 오류: " + e.getMessage());
        }
    }

    // ✅ 5. 파일 URL 생성 (외부 공유용)
    public String generateFileUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(60 * 60) // 1시간 유효
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("파일 URL 생성 오류: " + e.getMessage());
        }
    }

    // ✅ Pre-signed URL 생성 (파일 업로드)
    public String generatePresignedUploadUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)  // PUT 요청 (업로드)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(15, TimeUnit.MINUTES)  // 15분 동안 유효
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 Pre-signed URL 생성 오류: " + e.getMessage());
        }
    }

    // ✅ Pre-signed URL 생성 (파일 다운로드)
    public String generatePresignedDownloadUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)  // GET 요청 (다운로드)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(1, TimeUnit.HOURS)  // 1시간 동안 유효
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 Pre-signed URL 생성 오류: " + e.getMessage());
        }
    }
}
