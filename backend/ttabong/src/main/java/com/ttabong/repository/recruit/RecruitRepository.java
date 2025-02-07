package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Integer> {
    List<Recruit> findByTemplateId(Integer templateId);
}
