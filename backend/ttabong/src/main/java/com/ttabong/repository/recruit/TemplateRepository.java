package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.user.Organization;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {

    @Query("SELECT t FROM Template t WHERE (:cursor IS NULL OR t.id < :cursor) ORDER BY t.id DESC LIMIT :limit")
    List<Template> findAvailableTemplates(@Param("cursor") Integer cursor, @Param("limit") Integer limit);

    @Modifying
    @Query("UPDATE Template t SET t.title = :title, t.description = :description, t.activityLocation = :activityLocation, " +
            "t.contactName = :contactName, t.contactPhone = :contactPhone WHERE t.id = :templateId AND t.org = :org")
    void updateTemplate(@Param("templateId") Integer templateId,
                        @Param("org") Organization org,
                        @Param("title") String title,
                        @Param("description") String description,
                        @Param("activityLocation") String activityLocation,
                        @Param("contactName") String contactName,
                        @Param("contactPhone") String contactPhone);

    @Modifying
    @Query("UPDATE Template t SET t.isDeleted = true WHERE t.id IN :deleteTemplateIds")
    void deleteTemplates(@Param("deleteTemplateIds") List<Integer> deleteTemplateIds);


    @Query("SELECT t FROM Template t WHERE t.isDeleted = false AND t.group.id = :groupId")
    List<Template> findTemplatesByGroupId(@Param("groupId") Integer groupId);

    @Modifying
    @Transactional
    @Query("UPDATE Template t SET t.image.id = :imageId WHERE t.id = :templateId")
    void updateTemplateImage(@Param("templateId") Integer templateId, @Param("imageId") Integer imageId);

}
