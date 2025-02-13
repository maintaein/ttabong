package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Application;
import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.recruit.VolunteerReaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit, Integer> {

    List<Recruit> findByTemplateId(Integer templateId);

    @Query("SELECT r FROM Recruit r " +
            "JOIN FETCH r.template t " +
            "JOIN FETCH t.org o " +
            "WHERE (:cursor IS NULL OR r.id < :cursor) " +
            "AND o.user.id = :userId " +
            "AND r.isDeleted = false " +
            "ORDER BY r.id DESC")
    List<Recruit> findAvailableRecruits(@Param("cursor") Integer cursor, @Param("userId") Integer userId,  Pageable pageable);

    @Modifying
    @Query("UPDATE Recruit r " +
            "SET r.isDeleted = true " +
            "WHERE r.id IN :deleteIds " +
            "AND EXISTS ( " +
            "    SELECT t FROM Template t " +
            "    WHERE t.id = r.template.id " +
            "    AND t.org.user.id = :userId " +
            ") " +
            "AND r.isDeleted = false")
    int markAsDeleted(@Param("deleteIds") List<Integer> deleteIds, @Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE Recruit r " +
            "SET r.deadline = :deadline, " +
            "r.activityDate = :activityDate, " +
            "r.activityStart = :activityStart, " +
            "r.activityEnd = :activityEnd, " +
            "r.maxVolunteer = :maxVolunteer, " +
            "r.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE r.id = :recruitId")
    void updateRecruit(
            @Param("recruitId") Integer recruitId,
            @Param("deadline") Instant deadline,
            @Param("activityDate") Date activityDate,
            @Param("activityStart") BigDecimal activityStart,
            @Param("activityEnd") BigDecimal activityEnd,
            @Param("maxVolunteer") Integer maxVolunteer
    );

    @Modifying
    @Query("UPDATE Recruit r SET r.status = 'RECRUITMENT_CLOSED' WHERE r.id = :closeId")
    void closeRecruit(@Param("closeId") Integer closeId);


    // VolRecruit---------------------------------------------------------

    // 특정 모집 공고 ID로 조회 (isDeleted = false 조건 포함)
    @Query("SELECT r FROM Recruit r WHERE r.id = :recruitId AND r.isDeleted = false")
    Optional<Recruit> findByRecruitId(@Param("recruitId") Integer recruitId);

    // 특정 봉사자(userId)와 모집 공고(recruitId)로 신청 기록 조회 (isDeleted = false 조건 포함)
    @Query("SELECT a FROM Application a WHERE a.volunteer.id = :userId AND a.recruit.id = :recruitId AND a.isDeleted = false")
    Optional<Application> findByVolunteerAndRecruit(@Param("userId") int userId, @Param("recruitId") int recruitId);

    // 특정 봉사자(userId)가 신청한 모집 공고 목록 조회 (isDeleted = false 조건 포함)
    @Query("SELECT a FROM Application a WHERE a.volunteer.id = :userId AND a.isDeleted = false ORDER BY a.createdAt DESC")
    List<Application> findApplicationsByVolunteer(@Param("userId") int userId);

    // 특정 봉사자(userId)가 좋아요한 모집 공고 리스트 조회 (isDeleted = false 조건 포함)
    @Query("SELECT vr FROM VolunteerReaction vr WHERE vr.volunteer.id = :userId AND vr.isLike = true AND vr.isDeleted = false")
    List<VolunteerReaction> findLikedTemplatesByVolunteer(@Param("userId") int userId);




}
