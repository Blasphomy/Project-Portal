package com.project.learning.dto;

import com.project.learning.domain.skilltree.QuestDependency;
import com.project.learning.domain.skilltree.QuestNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for returning skill tree with quest statuses computed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillTreeWithStatus {
    private UUID id;
    private String title;
    private String description;
    private String userGoal;
    private List<QuestNode> nodes;
    private List<QuestDependency> dependencies;
}
