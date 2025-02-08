package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.TemplateImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateImageRepository extends JpaRepository<TemplateImage, Integer> {
    List<TemplateImage> findByTemplateId(Integer templateId);
}

