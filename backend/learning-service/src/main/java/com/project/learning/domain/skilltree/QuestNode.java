
package com.project.learning.domain.skilltree;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("quest_nodes")
@Getter
@Setter
public class QuestNode {

    @Id
    private Long id;

    @Column("quest_id")
    private String questId;

    private String title;

    private String description;

    private String category;

    @Column("skill_tree_id")
    private UUID skillTreeId;
}
