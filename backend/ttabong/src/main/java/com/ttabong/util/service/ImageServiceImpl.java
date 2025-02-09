package com.ttabong.util.service;

import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.sns.ReviewImage;
import com.ttabong.repository.sns.ReviewImageRepository;
import com.ttabong.util.CacheUtil;
import com.ttabong.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final ReviewImageRepository reviewImageRepository;
    private final CacheUtil cacheUtil;
    private final ImageUtil imageUtil;

    // 미리 슬롯 10개 받아두기
    @Transactional
    public void initializeReviewImagesForTemplate(Template template) {
        for (int i = 0; i < 10; i++) {
            reviewImageRepository.save(ReviewImage.builder()
                    .template(template)
                    .imageUrl(null)
                    .isThumbnail(i == 0)
                    .isDeleted(false)
                    .createdAt(Instant.now())
                    .build());
        }
    }

    @Transactional
    public void updateReviewImages(Integer templateId, List<String> presignedUrls) {
        List<ReviewImage> reviewImages = reviewImageRepository.findByTemplateId(templateId);

        for (int i = 0; i < presignedUrls.size(); i++) {
            if (i >= reviewImages.size()) break;

            String objectPath = cacheUtil.findObjectPath(presignedUrls.get(i));

            if (objectPath == null) {
                continue;
            }

            reviewImages.get(i).setImageUrl(objectPath);
            reviewImages.get(i).setIsDeleted(false);
            reviewImages.get(i).setThumbnail(i == 0);
        }

        reviewImageRepository.saveAll(reviewImages);
    }


    @Transactional
    public void updateThumbnailImage(Integer templateId) {
        reviewImageRepository.resetThumbnailImages(templateId);
        Optional<ReviewImage> firstImage = reviewImageRepository.findFirstByTemplateOrderByIdAsc(
                new Template(templateId)
        );
        firstImage.ifPresent(image -> reviewImageRepository.setThumbnailImage(image.getId()));
    }

    public List<String> getTemplateImageUrls(Integer templateId) {
        return reviewImageRepository.findByTemplateId(templateId)
                .stream()
                .map(ReviewImage::getImageUrl)
                .filter(Objects::nonNull)
                .map(imageUrl -> {
                    try {
                        return imageUtil.getPresignedDownloadUrl(imageUrl);
                    } catch (Exception e) {
                        throw new RuntimeException("MinIO Presigned URL 생성 중 오류 발생", e);
                    }
                })
                .collect(Collectors.toList());
    }

}
