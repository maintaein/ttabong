package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.VolunteerReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 특정 "좋아요" 취소
    @Query("UPDATE VolunteerReaction v SET v.isDeleted = TRUE WHERE v.id IN (:reactionIds)")
    void deleteAllById(List<Integer> reactionIds);

}
