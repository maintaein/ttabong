package com.ttabong.repository.recruit;

import com.ttabong.entity.recruit.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Integer> {
    @Query("SELECT t FROM Template t WHERE (:cursor IS NULL OR t.id < :cursor) ORDER BY t.id DESC LIMIT :limit")
    List<Template> findAvailableTemplates(@Param("cursor") Integer cursor, @Param("limit") Integer limit);
}
