package com.ttabong.repository.user;

import com.ttabong.entity.user.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {

    // 기관의 공고 _ 봉사자 관리 부분
    @Transactional
    @Modifying
    @Query("UPDATE Volunteer v SET v.recommendedCount = v.recommendedCount + 1 WHERE v.id = :volunteerId")
    void incrementRecommendation(@Param("volunteerId") Integer volunteerId);

    @Transactional
    @Modifying
    @Query("UPDATE Volunteer v SET v.notRecommendedCount = v.notRecommendedCount + 1 WHERE v.id = :volunteerId")
    void incrementNotRecommendation(@Param("volunteerId") Integer volunteerId);

    // 봉사자 쿼리메소드 적기
}
