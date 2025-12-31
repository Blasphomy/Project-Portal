
package com.project.learning.domain.skilltree;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("quest_dependencies")
@Getter
@Setter
public class QuestDependency {

    @Id
    private Long id;

    @Column("source_quest_id")
    private String sourceQuestId;

    @Column("target_quest_id")
    private String targetQuestId;

    @Column("skill_tree_id")
    private UUID skillTreeId;
}
