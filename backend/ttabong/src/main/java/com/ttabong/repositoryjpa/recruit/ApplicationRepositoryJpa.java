package com.ttabong.repositoryjpa.recruit;

import com.ttabong.entity.recruit.Application;
import com.ttabong.entity.recruit.Recruit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface ApplicationRepositoryJpa extends JpaRepository<Application, Integer> {

    @EntityGraph(attributePaths = {"volunteer.user"})
    List<Application> findByRecruitId(Integer recruitId);

    @EntityGraph(attributePaths = {"volunteer.user"})
    Optional<Application> findById(Integer id);

    default Map<Integer, Application> findByRecruitIdMap(Integer recruitId){
        return findByRecruitId(recruitId).stream().collect(Collectors.toMap(application -> application.getVolunteer().getId(), application -> application));
    }
}
