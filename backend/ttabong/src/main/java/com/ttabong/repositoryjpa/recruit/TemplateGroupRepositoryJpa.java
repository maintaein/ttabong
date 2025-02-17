package com.ttabong.repositoryjpa.recruit;

import com.ttabong.entity.recruit.TemplateGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateGroupRepositoryJpa extends JpaRepository<TemplateGroup, Integer> {

    @EntityGraph(attributePaths = {"org"})
    Optional<TemplateGroup> findByIdAndIsDeletedFalse(Integer groupId);

    @EntityGraph(attributePaths = {})
    Optional<TemplateGroup> findByOrgIdAndGroupNameAndIsDeletedFalse(Integer orgId, String groupName);
}
