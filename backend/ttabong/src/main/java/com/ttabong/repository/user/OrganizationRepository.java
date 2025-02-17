package com.ttabong.repository.user;

import com.ttabong.entity.user.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    boolean existsByUserId(Integer userId);
    Optional<Organization> findByUserId(Integer userId);

}
