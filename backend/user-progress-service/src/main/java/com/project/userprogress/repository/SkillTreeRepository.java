package com.project.userprogress.repository;

import com.project.userprogress.models.SkillTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillTreeRepository extends JpaRepository<SkillTree, Long> {
    List<SkillTree> findByUserId(Long userId);
}
