package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Template;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {

    // 1.
    @Query("SELECT t FROM Template t " +
            "JOIN FETCH t.org o " +
            "WHERE (:cursor IS NULL OR :cursor = 0 OR t.id < :cursor) " +
            "AND o.user.id = :userId " +
            "AND t.isDeleted = false " +
            "AND EXISTS ( " +
            "   SELECT 1 FROM Recruit r " +
            "   WHERE r.template.id = t.id " +
            "   AND r.isDeleted = false " +
            "   AND r.status = 'RECRUITING' " +
            ") " +
            "ORDER BY t.id DESC")
    List<Template> findAvailableTemplates(@Param("cursor") Integer cursor,
                                          @Param("userId") Integer userId,
                                          Pageable pageable);

    @Modifying
    @Query("UPDATE Template t " +
            "SET t.title = :title, " +
            "t.description = :description, " +
            "t.activityLocation = :activityLocation, " +
            "t.contactName = :contactName, " +
            "t.contactPhone = :contactPhone " +
            "WHERE t.id = :templateId " +
            "AND t.org.id = :orgId")
    void updateTemplate(@Param("templateId") Integer templateId,
                        @Param("orgId") Integer orgId,
                        @Param("title") String title,
                        @Param("description") String description,
                        @Param("activityLocation") String activityLocation,
                        @Param("contactName") String contactName,
                        @Param("contactPhone") String contactPhone);


    @Modifying
    @Query("UPDATE Template t SET t.isDeleted = true WHERE t.id IN :deleteTemplateIds AND t.org.id = :orgId")
    void deleteTemplates(@Param("deleteTemplateIds") List<Integer> deleteTemplateIds, @Param("orgId") Integer orgId);

    @Query("SELECT t FROM Template t WHERE t.isDeleted = false AND t.group.id = :groupId")
    List<Template> findTemplatesByGroupId(@Param("groupId") Integer groupId);


    @Query("SELECT t FROM Template t WHERE t.id IN :templateIds AND t.org.id = :orgId AND t.isDeleted = false")
    List<Template> findByIdsAndOrgId(@Param("templateIds") List<Integer> templateIds, @Param("orgId") Integer orgId);


    @Query("SELECT r.template.id FROM Recruit r WHERE r.id = :recruitId")
    Integer findTemplateIdByRecruitId(@Param("recruitId") Integer recruitId);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Template t SET t.image.id = :imageId WHERE t.id = :templateId")
//    void updateTemplateImage(@Param("templateId") Integer templateId, @Param("imageId") Integer imageId);



    // vol-recruit를 위해 추가 --------------------------------------------
    // 특정 templateId 이후의 모집 공고 리스트 조회 (isDeleted = false 조건 포함)
    @Query("SELECT t FROM Template t WHERE t.id > :cursor AND t.isDeleted = false ORDER BY t.id ASC LIMIT :limit")
    List<Template> findTemplatesAfterCursor(@Param("cursor") Integer cursor, @Param("limit") Integer limit);

    // 가장 처음 limit 개수만큼의 모집 공고 조회 (isDeleted = false 조건 포함)
    @Query("SELECT t FROM Template t WHERE t.isDeleted = false ORDER BY t.id ASC LIMIT :limit")
    List<Template> findTopNTemplates(@Param("limit") Integer limit);

    // 특정 템플릿 ID로 템플릿 조회 (isDeleted = false 조건 포함)
    @Query("SELECT t FROM Template t WHERE t.id = :templateId AND t.isDeleted = false")
    Optional<Template> findById(@Param("templateId") Integer templateId);



}
