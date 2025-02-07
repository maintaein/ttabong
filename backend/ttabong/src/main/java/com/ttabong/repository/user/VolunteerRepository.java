package com.ttabong.repository.user;

import com.ttabong.entity.user.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {
    // 봉사자 쿼리메소드 적기
}
