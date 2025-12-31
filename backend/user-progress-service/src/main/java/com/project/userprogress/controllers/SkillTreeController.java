package com.project.userprogress.controllers;

import com.project.userprogress.models.SkillTree;
import com.project.userprogress.services.SkillTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-trees")
public class SkillTreeController {

    private final SkillTreeService skillTreeService;

    @Autowired
    public SkillTreeController(SkillTreeService skillTreeService) {
        this.skillTreeService = skillTreeService;
    }

    @PostMapping
    public ResponseEntity<SkillTree> createSkillTree(@RequestBody SkillTree skillTree) {
        SkillTree createdSkillTree = skillTreeService.createSkillTree(skillTree);
        return ResponseEntity.ok(createdSkillTree);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SkillTree>> getSkillTreesByUserId(@PathVariable Long userId) {
        List<SkillTree> skillTrees = skillTreeService.getSkillTreesByUserId(userId);
        return ResponseEntity.ok(skillTrees);
    }
}
