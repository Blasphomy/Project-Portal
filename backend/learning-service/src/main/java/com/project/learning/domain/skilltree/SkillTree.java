
package com.project.learning.domain.skilltree;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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

}
