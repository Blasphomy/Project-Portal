
package com.project.learning.repository.skilltree;

import com.project.learning.domain.skilltree.QuestNode;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface QuestNodeRepository extends R2dbcRepository<QuestNode, UUID> {

    /**
     * Find all quest nodes for a skill tree
     */
    Flux<QuestNode> findBySkillTreeId(UUID skillTreeId);

    /**
     * Find a specific quest node by skill tree ID and quest ID
     */
    Mono<QuestNode> findBySkillTreeIdAndQuestId(UUID skillTreeId, String questId);

    /**
     * Find quest nodes by skill tree ID and status
     */
    Flux<QuestNode> findBySkillTreeIdAndStatus(UUID skillTreeId, String status);
}
