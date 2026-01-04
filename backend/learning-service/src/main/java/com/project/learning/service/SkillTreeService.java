
package com.project.learning.service;

import com.project.learning.domain.skilltree.QuestDependency;
import com.project.learning.domain.skilltree.QuestNode;
import com.project.learning.domain.skilltree.SkillTree;
import com.project.learning.dto.GeneratedSkillTree;
import com.project.learning.dto.SkillTreeWithStatus;
import com.project.learning.repository.skilltree.QuestDependencyRepository;
import com.project.learning.repository.skilltree.QuestNodeRepository;
import com.project.learning.repository.skilltree.SkillTreeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillTreeService {

    private final SkillTreeRepository skillTreeRepository;
    private final QuestNodeRepository questNodeRepository;
    private final QuestDependencyRepository questDependencyRepository;

    /**
     * Save AI-generated skill tree to database and compute initial quest statuses
     */
    @Transactional
    public Mono<SkillTree> saveGeneratedSkillTree(GeneratedSkillTree generatedSkillTree, String userId) {
        SkillTree skillTree = new SkillTree();
        skillTree.setUserId(userId);
        skillTree.setTitle(generatedSkillTree.getTitle());
        skillTree.setDescription(generatedSkillTree.getDescription());
        skillTree.setUserGoal(
                generatedSkillTree.getUserGoal() != null ? generatedSkillTree.getUserGoal() : "Custom learning path");
        skillTree.setStatus("active");

        return skillTreeRepository.save(skillTree)
                .flatMap(savedSkillTree -> {
                    List<QuestNode> questNodes = generatedSkillTree.getNodes().stream().map(nodeDto -> {
                        QuestNode questNode = new QuestNode();
                        questNode.setSkillTreeId(savedSkillTree.getId());
                        questNode.setQuestId(nodeDto.getId());
                        questNode.setTitle(nodeDto.getTitle());
                        questNode.setDescription(nodeDto.getDescription());
                        questNode.setCategory(nodeDto.getCategory());
                        // Initially all quests are locked except root quests (computed below)
                        questNode.setStatus("locked");
                        questNode.setPositionX(0f);
                        questNode.setPositionY(0f);
                        return questNode;
                    }).collect(Collectors.toList());

                    List<QuestDependency> questDependencies = generatedSkillTree.getEdges().stream().map(edgeDto -> {
                        QuestDependency dependency = new QuestDependency();
                        dependency.setSkillTreeId(savedSkillTree.getId());
                        dependency.setSourceQuestId(edgeDto.getSource());
                        dependency.setTargetQuestId(edgeDto.getTarget());
                        return dependency;
                    }).collect(Collectors.toList());

                    // Save nodes and dependencies
                    return questNodeRepository.saveAll(questNodes)
                            .collectList()
                            .flatMap(savedNodes -> questDependencyRepository.saveAll(questDependencies).collectList())
                            .flatMap(savedDeps -> {
                                // Unlock root quests (those with no prerequisites)
                                Set<String> questsWithDeps = new HashSet<>();
                                for (QuestDependency dep : savedDeps) {
                                    questsWithDeps.add(dep.getTargetQuestId());
                                }

                                List<Mono<QuestNode>> unlockOps = new ArrayList<>();
                                for (QuestNode node : questNodes) {
                                    if (!questsWithDeps.contains(node.getQuestId())) {
                                        // Root quest - unlock it
                                        node.setStatus("unlocked");
                                        unlockOps.add(questNodeRepository.save(node));
                                    }
                                }

                                return Flux.concat(unlockOps)
                                        .then(Mono.just(savedSkillTree));
                            });
                });
    }

    /**
     * Get user's active skill tree with all nodes and dependencies
     */
    public Mono<SkillTreeWithStatus> getUserSkillTree(String userId) {
        return skillTreeRepository.findByUserIdAndStatus(userId, "active")
                .flatMap(skillTree -> {
                    Mono<List<QuestNode>> nodesMono = questNodeRepository
                            .findBySkillTreeId(skillTree.getId())
                            .collectList();

                    Mono<List<QuestDependency>> depsMono = questDependencyRepository
                            .findBySkillTreeId(skillTree.getId())
                            .collectList();

                    return Mono.zip(nodesMono, depsMono)
                            .map(tuple -> new SkillTreeWithStatus(
                                    skillTree.getId(),
                                    skillTree.getTitle(),
                                    skillTree.getDescription(),
                                    skillTree.getUserGoal(),
                                    tuple.getT1(), // nodes
                                    tuple.getT2() // dependencies
                    ));
                });
    }

    /**
     * Complete a quest and unlock dependent quests
     */
    @Transactional
    public Mono<QuestNode> completeQuest(UUID skillTreeId, String questId, String userId) {
        // First verify the skill tree belongs to this user
        return skillTreeRepository.findById(skillTreeId)
                .filter(tree -> tree.getUserId().equals(userId))
                .switchIfEmpty(Mono.error(new RuntimeException("Skill tree not found or unauthorized")))
                .flatMap(tree -> {
                    // Find and mark the quest as completed
                    return questNodeRepository.findBySkillTreeIdAndQuestId(skillTreeId, questId)
                            .flatMap(questNode -> {
                                questNode.setStatus("completed");
                                questNode.setUpdatedAt(LocalDateTime.now());
                                return questNodeRepository.save(questNode);
                            })
                            .flatMap(completedQuest -> {
                                // Find all quests that depend on this one
                                return questDependencyRepository.findBySkillTreeIdAndSourceQuestId(skillTreeId, questId)
                                        .collectList()
                                        .flatMap(dependencies -> {
                                            if (dependencies.isEmpty()) {
                                                return Mono.just(completedQuest);
                                            }

                                            // For each dependent quest, check if all prerequisites are completed
                                            List<Mono<QuestNode>> unlockOps = new ArrayList<>();
                                            for (QuestDependency dep : dependencies) {
                                                unlockOps.add(checkAndUnlockQuest(skillTreeId, dep.getTargetQuestId()));
                                            }

                                            return Flux.concat(unlockOps)
                                                    .then(Mono.just(completedQuest));
                                        });
                            });
                });
    }

    /**
     * Check if a quest can be unlocked (all prerequisites completed)
     */
    private Mono<QuestNode> checkAndUnlockQuest(UUID skillTreeId, String targetQuestId) {
        return questNodeRepository.findBySkillTreeIdAndQuestId(skillTreeId, targetQuestId)
                .flatMap(targetNode -> {
                    if ("completed".equals(targetNode.getStatus()) || "unlocked".equals(targetNode.getStatus())) {
                        return Mono.just(targetNode); // Already unlocked or completed
                    }

                    // Get all prerequisites for this quest
                    return questDependencyRepository.findBySkillTreeIdAndTargetQuestId(skillTreeId, targetQuestId)
                            .collectList()
                            .flatMap(prerequisites -> {
                                if (prerequisites.isEmpty()) {
                                    // No prerequisites, can unlock
                                    targetNode.setStatus("unlocked");
                                    return questNodeRepository.save(targetNode);
                                }

                                // Check if all prerequisite quests are completed
                                List<Mono<QuestNode>> prereqNodes = prerequisites.stream()
                                        .map(dep -> questNodeRepository.findBySkillTreeIdAndQuestId(skillTreeId,
                                                dep.getSourceQuestId()))
                                        .collect(Collectors.toList());

                                return Flux.concat(prereqNodes)
                                        .collectList()
                                        .flatMap(nodes -> {
                                            boolean allCompleted = nodes.stream()
                                                    .allMatch(node -> "completed".equals(node.getStatus()));

                                            if (allCompleted) {
                                                targetNode.setStatus("unlocked");
                                                return questNodeRepository.save(targetNode);
                                            } else {
                                                return Mono.just(targetNode); // Keep locked
                                            }
                                        });
                            });
                });
    }

    /**
     * Get all unlocked quests for a skill tree
     */
    public Flux<QuestNode> getUnlockedQuests(UUID skillTreeId) {
        return questNodeRepository.findBySkillTreeId(skillTreeId)
                .filter(node -> "unlocked".equals(node.getStatus()));
    }
}
