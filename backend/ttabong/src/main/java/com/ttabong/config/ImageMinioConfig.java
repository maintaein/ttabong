package com.ttabong.config;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageMinioConfig {
    private static final Logger logger = LoggerFactory.getLogger(Object.class);
    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.bucket-name}")
    private String bucketName;
    private MinioClient minioClient;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://127.0.0.1:9000")  // MinIO 서버 주소
                .credentials("ttabong", "ttabong-bongteum")  // MinIO 계정 정보
                .build();
    }
}
