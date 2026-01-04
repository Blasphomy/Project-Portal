
package com.project.learning.repository.skilltree;

import com.project.learning.domain.skilltree.QuestDependency;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface QuestDependencyRepository extends R2dbcRepository<QuestDependency, UUID> {

    /**
     * Find all dependencies for a skill tree
     */
    Flux<QuestDependency> findBySkillTreeId(UUID skillTreeId);

    /**
     * Find all dependencies where the given quest is the source (prerequisite)
     */
    Flux<QuestDependency> findBySkillTreeIdAndSourceQuestId(UUID skillTreeId, String sourceQuestId);

    /**
     * Find all dependencies where the given quest is the target (depends on others)
     */
    Flux<QuestDependency> findBySkillTreeIdAndTargetQuestId(UUID skillTreeId, String targetQuestId);
}
