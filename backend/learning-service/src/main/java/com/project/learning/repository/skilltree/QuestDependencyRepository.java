
package com.project.learning.repository.skilltree;

import com.project.learning.domain.skilltree.QuestDependency;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestDependencyRepository extends R2dbcRepository<QuestDependency, Long> {
}
