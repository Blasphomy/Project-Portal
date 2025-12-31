package com.project.userprogress.services;

import com.project.userprogress.models.SkillTree;
import com.project.userprogress.repository.SkillTreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillTreeService {

    private final SkillTreeRepository skillTreeRepository;

    @Autowired
    public SkillTreeService(SkillTreeRepository skillTreeRepository) {
        this.skillTreeRepository = skillTreeRepository;
    }

    public SkillTree createSkillTree(SkillTree skillTree) {
        return skillTreeRepository.save(skillTree);
    }

    public List<SkillTree> getSkillTreesByUserId(Long userId) {
        return skillTreeRepository.findByUserId(userId);
    }
}
