package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.TemplateGroup;
import com.ttabong.entity.user.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateGroupRepository extends JpaRepository<TemplateGroup, Integer> {

    @Modifying
    @Query("UPDATE TemplateGroup tg SET tg.groupName = :groupName WHERE tg.id = :groupId AND tg.org.id = :orgId")
    void updateGroup(@Param("groupId") Integer groupId, @Param("orgId") Integer orgId, @Param("groupName") String groupName);

    @Modifying
    @Query("UPDATE TemplateGroup tg SET tg.isDeleted = true WHERE tg.id = :groupId AND tg.org.id = :orgId")
    void deleteGroupByIdAndOrg(@Param("groupId") Integer groupId, @Param("orgId") Integer orgId);

    @Query("SELECT tg FROM TemplateGroup tg WHERE tg.isDeleted = false")
    List<TemplateGroup> findGroups(Pageable pageable);

    boolean existsByOrgAndGroupName(Organization org, String groupName);
}
