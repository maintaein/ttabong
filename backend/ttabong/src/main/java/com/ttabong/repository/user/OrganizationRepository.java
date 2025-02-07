package com.ttabong.repository.user;

import com.ttabong.entity.user.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    //기관 쿼리메소드 적기
}
