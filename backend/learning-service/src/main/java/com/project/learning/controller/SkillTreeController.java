
package com.project.learning.controller;

import com.project.learning.domain.skilltree.SkillTree;
import com.project.learning.dto.GeneratedSkillTree;
import com.project.learning.service.SkillTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/skill-trees")
@RequiredArgsConstructor
public class SkillTreeController {

    private final SkillTreeService skillTreeService;

    @PostMapping
    public Mono<ResponseEntity<SkillTree>> createSkillTree(
            @RequestBody GeneratedSkillTree generatedSkillTree,
            @RequestHeader("X-User-Id") String userId) {
        
        return skillTreeService.saveGeneratedSkillTree(generatedSkillTree, userId)
                .map(savedSkillTree -> new ResponseEntity<>(savedSkillTree, HttpStatus.CREATED));
    }
}
