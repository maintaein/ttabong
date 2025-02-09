package com.ttabong.repository.sns;

import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.sns.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {

    Optional<ReviewImage> findFirstByTemplateOrderByIdAsc(Template template);

    @Modifying
    @Query("UPDATE ReviewImage ri SET ri.isThumbnail = true WHERE ri.id = :imageId")
    void setThumbnailImage(@Param("imageId") Integer imageId);

    @Modifying
    @Query("UPDATE ReviewImage ri SET ri.isThumbnail = false WHERE ri.template.id = :templateId")
    void resetThumbnailImages(@Param("templateId") Integer templateId);

}
