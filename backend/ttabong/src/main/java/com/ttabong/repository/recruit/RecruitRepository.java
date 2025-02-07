package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Integer> {
    List<Recruit> findByTemplateId(Integer templateId);

    @Query("SELECT r FROM Recruit r WHERE (:cursor IS NULL OR r.id < :cursor) ORDER BY r.id DESC LIMIT :limit")
    List<Recruit> findAvailableRecruits(@Param("cursor") Integer cursor, @Param("limit") Integer limit);
}
