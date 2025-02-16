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
import java.time.LocalDate;
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
    Optional<Recruit> findByRecruitId(@Param("recruitId") Integer recruitId);


    @Query("""
        SELECT r FROM Recruit r 
        JOIN FETCH r.template t
        JOIN FETCH t.org o
        JOIN FETCH t.group g
        WHERE 
            (:searchKeyword IS NULL OR t.title LIKE %:searchKeyword% OR o.orgName LIKE %:searchKeyword%)
            AND (:status IS NULL OR r.status = :status)
            AND (:region IS NULL OR t.activityLocation LIKE %:region%)
            AND ((:startDate IS NULL OR :endDate IS NULL) OR (r.activityDate BETWEEN :startDate AND :endDate))
            AND (:cursor IS NULL OR t.id > :cursor)
        ORDER BY t.id ASC
        LIMIT :limit
    """)
    List<Recruit> searchRecruits(
            @Param("searchKeyword") String searchKeyword,
            @Param("status") String status,
            @Param("region") String region,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("cursor") Long cursor,
            @Param("limit") int limit
    );

    // VolRecruit---------------------------------------------------------

    // 특정 모집 공고 조회
    Optional<Recruit> findByIdAndIsDeletedFalse(Integer recruitId);



    @Query("SELECT t.group.id FROM Recruit r JOIN r.template t WHERE r.id = :recruitId")
    Optional<Integer> findGroupIdByRecruitId(@Param("recruitId") Integer recruitId);
}
