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
    // 특정 봉사자가 신청한 봉사 리스트 조회
    @Query("SELECT a FROM Application a WHERE a.volunteer = :volunteerId AND a.isDeleted = false ORDER BY a.createdAt DESC")
    List<Application> findApplicationsByVolunteer(@Param("volunteerId") Volunteer volunteer);

}
