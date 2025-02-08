package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.volunteer v " +
            "JOIN FETCH v.user u " +
            "WHERE a.recruit.id = :recruitId")
    List<Application> findByRecruitIdWithUser(@Param("recruitId") Integer recruitId);
}
