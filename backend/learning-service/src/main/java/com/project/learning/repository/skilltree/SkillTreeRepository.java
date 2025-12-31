
package com.project.learning.repository.skilltree;

import com.project.learning.domain.skilltree.SkillTree;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SkillTreeRepository extends R2dbcRepository<SkillTree, UUID> {

    /**
     * Finds a skill tree by the user ID.
     * Assumes a user can only have one active skill tree at a time.
     *
     * @param userId The ID of the user.
     * @return A Mono containing the user's skill tree if it exists.
     */
    Mono<SkillTree> findByUserId(String userId);
}
