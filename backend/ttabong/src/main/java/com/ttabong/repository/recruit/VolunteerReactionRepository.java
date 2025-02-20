package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.recruit.VolunteerReaction;
import com.ttabong.entity.user.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VolunteerReactionRepository extends JpaRepository<VolunteerReaction, Integer> {

    // 사용자가 "좋아요"한 템플릿 목록 조회
    @Query("SELECT DISTINCT v FROM VolunteerReaction v " +
            "JOIN FETCH v.recruit r " +
            "JOIN FETCH r.template t " +
            "JOIN FETCH t.group g " +
            "WHERE v.volunteer.user.id = :userId " +
            "AND v.isLike = TRUE " +
            "AND v.isDeleted = FALSE " +
            "AND v.id > :cursor " +
            "ORDER BY v.createdAt DESC " +
            "Limit :limit")
    List<VolunteerReaction> findLikedTemplatesByUserId(
            @Param("userId") Integer userId,
            @Param("cursor") Integer cursor,
            @Param("limit") Integer limit
    );

    @Modifying
    @Transactional
    @Query("UPDATE VolunteerReaction v SET v.isDeleted = TRUE WHERE v.id IN :reactionIds")
    int softDeleteByIds(@Param("reactionIds") List<Integer> reactionIds);

    @Query("SELECT COUNT(vr) > 0 FROM VolunteerReaction vr " +
            "JOIN vr.recruit r " +
            "WHERE vr.volunteer = :volunteer " +
            "AND r.template = :template " +
            "AND vr.isDeleted = false " +
            "AND r.isDeleted = false")
    boolean existsByVolunteerAndRecruitTemplate(Volunteer volunteer, Template template);

    //같은 템플릿에 대하여 새로 리액션을 할 시에 기존의 리액션 소프트삭제
    @Modifying
    @Transactional
    @Query("UPDATE VolunteerReaction vr SET vr.isDeleted = TRUE " +
            "WHERE vr.volunteer = :volunteer AND vr.isDeleted = FALSE " +
            "AND vr.recruit.id IN (SELECT r.id FROM Recruit r WHERE r.template = :template)")
    void softDeleteByVolunteerAndTemplate(@Param("volunteer") Volunteer volunteer, @Param("template") Template template);

    @Query("""
    SELECT DISTINCT t FROM Template t 
    WHERE t.id IN (
        SELECT DISTINCT r.template.id FROM VolunteerReaction vr
        JOIN vr.recruit r 
        WHERE vr.volunteer.user.id = :userId 
        AND vr.isLike = TRUE 
        AND vr.isDeleted = FALSE 
        AND vr.id < :cursor
        ORDER BY vr.id DESC
    )
    ORDER BY t.id DESC Limit :limit
    """)
    List<Template> findLikedTemplateListByUserId(
            @Param("userId") Integer userId, @Param("cursor") Integer cursor, @Param("limit") Integer limit);

    @Query(value = "SELECT r.template_id AS templateId, MAX(vr.reaction_id) AS lastReactionId " +
            "FROM Volunteer_reaction vr " +
            "JOIN Recruit r ON vr.Recruit_id = r.recruit_id " +
            "WHERE vr.volunteer_id = :userId " +
            "  AND vr.is_like = true " +
            "  AND vr.is_deleted = false " +
            "GROUP BY r.template_id " +
            "ORDER BY lastReactionId DESC " +
            "LIMIT :limit OFFSET :cursor", nativeQuery = true)
    List<Object[]> findDistinctLikedTemplates(@Param("userId") Integer userId,
                                              @Param("cursor") Integer cursor,
                                              @Param("limit") Integer limit);


    @Query("SELECT vr FROM VolunteerReaction vr " +
            "WHERE vr.volunteer = :volunteer " +
            "AND vr.isLike = true " +
            "AND vr.isDeleted = false " +
            "AND (:cursor = 0 OR vr.id < :cursor) " +
            "ORDER BY vr.id DESC Limit :limit")
    List<VolunteerReaction> findLikedReactions(@Param("volunteer") Volunteer volunteer,
                                               @Param("cursor") Integer cursor,
                                               @Param("limit") Integer limit);



}
