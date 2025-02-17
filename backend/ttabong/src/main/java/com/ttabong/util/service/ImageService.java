package com.ttabong.util.service;

import java.util.List;

public interface ImageService {
    void initializeReviewImages(Integer entityId, boolean isTemplate);

    void updateReviewImages(Integer templateId, List<String> presignedUrls);

    void updateThumbnailImage(Integer entityId, boolean isTemplate);

    List<String> getImageUrls(Integer entityId, boolean isTemplate);
}
