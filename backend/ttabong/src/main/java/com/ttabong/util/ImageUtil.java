package com.ttabong.util;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ImageUtil {

    private final MinioClient minioClient;

    @Value("${minio.url}")
    String minioUrl;
    @Value("${minio.access-key}")
    String accessKey;
    @Value("${minio.secret-key}")
    String secretKey;
    @Value("${minio.bucket-name}")
    String bucketName;

    /**
     * ✅ Presigned URL을 생성하여 클라이언트가 직접 업로드할 수 있도록 함
     *
     * @param objectName 저장할 파일명
     * @return presigned URL (PUT 방식)
     */
    public String getPresignedUploadUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.PUT)
                        .expiry(60, TimeUnit.MINUTES) // 10분 동안 유효
                        .build()
        );
    }

    /**
     * ✅ Presigned URL을 생성하여 클라이언트가 직접 다운로드할 수 있도록 함
     *
     * @param objectName 저장된 파일명
     * @return presigned URL (GET 방식)
     */
    public String getPresignedDownloadUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.GET)
                        .expiry(10, TimeUnit.MINUTES) // 10분 동안 유효
                        .build()
        );
    }

    /**
     * ✅ Presigned URL을 생성하여 클라이언트가 직접 삭제할 수 있도록 함
     *
     * @param objectName 삭제할 파일명
     * @return presigned URL (DELETE 방식)
     */
    public String getPresignedDeleteUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.DELETE)
                        .expiry(10, TimeUnit.MINUTES) // 10분 동안 유효
                        .build()
        );
    }

    public void deleteObject(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void validateTest(){
    }
}

