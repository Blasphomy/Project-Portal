package com.project.learning.domain.skilltree;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("quest_nodes")
@Getter
@Setter
public class QuestNode {

    @Id
    private UUID id;

    @Column("skill_tree_id")
    private UUID skillTreeId;

    @Column("quest_id")
    private String questId;

    private String title;

    private String description;

    private String category;

    private String status; // 'locked', 'unlocked', 'in_progress', 'completed'

    @Column("position_x")
    private Float positionX;

    @Column("position_y")
    private Float positionY;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
