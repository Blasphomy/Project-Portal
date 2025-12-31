
package com.project.learning.service;

import com.project.learning.domain.skilltree.QuestDependency;
import com.project.learning.domain.skilltree.QuestNode;
import com.project.learning.domain.skilltree.SkillTree;
import com.project.learning.dto.GeneratedSkillTree;
import com.project.learning.repository.skilltree.QuestDependencyRepository;
import com.project.learning.repository.skilltree.QuestNodeRepository;
import com.project.learning.repository.skilltree.SkillTreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillTreeService {

    private final SkillTreeRepository skillTreeRepository;
    private final QuestNodeRepository questNodeRepository;
    private final QuestDependencyRepository questDependencyRepository;

    public Mono<SkillTree> saveGeneratedSkillTree(GeneratedSkillTree generatedSkillTree, String userId) {
        SkillTree skillTree = new SkillTree();
        skillTree.setUserId(userId);
        skillTree.setTitle(generatedSkillTree.getTitle());
        skillTree.setDescription(generatedSkillTree.getDescription());

        return skillTreeRepository.save(skillTree)
                .flatMap(savedSkillTree -> {
                    List<QuestNode> questNodes = generatedSkillTree.getNodes().stream().map(nodeDto -> {
                        QuestNode questNode = new QuestNode();
                        questNode.setSkillTreeId(savedSkillTree.getId());
                        questNode.setQuestId(nodeDto.getId());
                        questNode.setTitle(nodeDto.getTitle());
                        questNode.setDescription(nodeDto.getDescription());
                        questNode.setCategory(nodeDto.getCategory());
                        return questNode;
                    }).collect(Collectors.toList());

                    List<QuestDependency> questDependencies = generatedSkillTree.getEdges().stream().map(edgeDto -> {
                        QuestDependency dependency = new QuestDependency();
                        dependency.setSkillTreeId(savedSkillTree.getId());
                        dependency.setSourceQuestId(edgeDto.getSource());
                        dependency.setTargetQuestId(edgeDto.getTarget());
                        return dependency;
                    }).collect(Collectors.toList());

                    return questNodeRepository.saveAll(questNodes)
                            .thenMany(questDependencyRepository.saveAll(questDependencies))
                            .then(Mono.just(savedSkillTree));
                });
    }
}
