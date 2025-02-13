package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Application;
import com.ttabong.entity.user.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.volunteer v " +
            "JOIN FETCH v.user u " +
            "WHERE a.recruit.id = :recruitId")
    List<Application> findByRecruitIdWithUser(@Param("recruitId") Integer recruitId);

    @Transactional
    @Modifying
    @Query("UPDATE Application a SET a.status = :status WHERE a.id = :applicationId")
    void updateApplicationStatus(@Param("applicationId") Integer applicationId, @Param("status") String status);

    // for VolRecruit -------------------------------------------
    // 사용자가 신청한 모집 공고 목록 조회 (소프트 삭제 적용)
    @Query("SELECT a FROM Application a WHERE a.volunteer.userId = :userId AND a.id > :cursor AND a.isDeleted = FALSE ORDER BY a.createdAt DESC")
    List<Application> findApplicationsByUserId(Integer userId, Integer cursor, Integer limit);


}
