
package com.project.learning.domain.skilltree;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("quest_dependencies")
@Getter
@Setter
public class QuestDependency {

    @Id
    private UUID id;

    @Column("skill_tree_id")
    private UUID skillTreeId;

    @Column("source_quest_id")
    private String sourceQuestId;

    @Column("target_quest_id")
    private String targetQuestId;

    @Column("created_at")
    private LocalDateTime createdAt;
}
