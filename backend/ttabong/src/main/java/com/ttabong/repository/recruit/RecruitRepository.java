package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Recruit;
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

    @Query("SELECT t.org.id FROM Recruit r JOIN r.template t WHERE r.id = :recruitId")
    Optional<Integer> findOrgIdByRecruitId(@Param("recruitId") Integer recruitId);

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

    @Query("SELECT r FROM Recruit r WHERE r.id = :recruitId AND r.isDeleted = false")
    Optional<Recruit> findByRecruitIdOrg(@Param("recruitId") Integer recruitId);

    // VolRecruit---------------------------------------------------------

    // 특정 모집 공고 조회
    Optional<Recruit> findByIdAndIsDeletedFalse(Integer recruitId);



    @Query("SELECT t.group.id FROM Recruit r JOIN r.template t WHERE r.id = :recruitId")
    Optional<Integer> findGroupIdByRecruitId(@Param("recruitId") Integer recruitId);
}
