package com.ttabong.repositoryjpa.recruit;

import com.ttabong.entity.recruit.TemplateGroup;
import com.ttabong.entity.user.Organization;
import jakarta.persistence.Entity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateGroupRepositoryJpa extends JpaRepository<TemplateGroup, Integer> {

    @EntityGraph(attributePaths = {"org"})
    Optional<TemplateGroup> findByIdAndIsDeletedFalse(Integer groupId);

    @EntityGraph(attributePaths = {})
    Optional<TemplateGroup> findByOrgIdAndByGroupNameAndIsDeletedFalse(Integer orgId, String groupName);
}
