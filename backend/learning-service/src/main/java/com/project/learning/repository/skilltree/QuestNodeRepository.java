
package com.project.learning.repository.skilltree;

import com.project.learning.domain.skilltree.QuestNode;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestNodeRepository extends R2dbcRepository<QuestNode, Long> {
}
