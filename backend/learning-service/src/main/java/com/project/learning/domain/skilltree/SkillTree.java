
package com.project.learning.domain.skilltree;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("skill_trees")
@Getter
@Setter
public class SkillTree {

    @Id
    private UUID id;

    @Column("user_id")
    private String userId;

    private String title;

    private String description;

    @Column("user_goal")
    private String userGoal;

    private String status; // 'active', 'completed', 'archived'

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
