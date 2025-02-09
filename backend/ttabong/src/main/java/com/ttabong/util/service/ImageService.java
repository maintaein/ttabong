package com.ttabong.util.service;

import com.ttabong.entity.recruit.Template;

import java.util.List;

public interface ImageService {
    void initializeReviewImagesForTemplate(Template template);
    void updateReviewImages(Integer templateId, List<String> presignedUrls);
    void updateThumbnailImage(Integer templateId);
    List<String> getTemplateImageUrls(Integer templateId);
}
