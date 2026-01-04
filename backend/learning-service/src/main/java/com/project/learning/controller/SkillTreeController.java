
package com.project.learning.controller;

import com.project.learning.domain.skilltree.QuestNode;
import com.project.learning.domain.skilltree.SkillTree;
import com.project.learning.dto.GeneratedSkillTree;
import com.project.learning.dto.SkillTreeWithStatus;
import com.project.learning.service.SkillTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/skill-trees")
@RequiredArgsConstructor
public class SkillTreeController {

    private final SkillTreeService skillTreeService;

    /**
     * Create a new skill tree from AI-generated data
     */
    @PostMapping
    public Mono<ResponseEntity<SkillTree>> createSkillTree(
            @RequestBody GeneratedSkillTree generatedSkillTree,
            @RequestHeader("X-User-Id") String userId) {

        return skillTreeService.saveGeneratedSkillTree(generatedSkillTree, userId)
                .map(savedSkillTree -> new ResponseEntity<>(savedSkillTree, HttpStatus.CREATED));
    }

    /**
     * Get user's active skill tree with all quest nodes and dependencies
     */
    @GetMapping("/user/{userId}")
    public Mono<ResponseEntity<SkillTreeWithStatus>> getUserSkillTree(@PathVariable String userId) {
        return skillTreeService.getUserSkillTree(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Mark a quest as completed and unlock dependent quests
     */
    @PostMapping("/{skillTreeId}/quests/{questId}/complete")
    public Mono<ResponseEntity<QuestNode>> completeQuest(
            @PathVariable UUID skillTreeId,
            @PathVariable String questId,
            @RequestHeader("X-User-Id") String userId) {

        return skillTreeService.completeQuest(skillTreeId, questId, userId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build()));
    }

    /**
     * Get all currently unlocked quests for a skill tree
     */
    @GetMapping("/{skillTreeId}/unlocked-quests")
    public Flux<QuestNode> getUnlockedQuests(@PathVariable UUID skillTreeId) {
        return skillTreeService.getUnlockedQuests(skillTreeId);
    }
}
