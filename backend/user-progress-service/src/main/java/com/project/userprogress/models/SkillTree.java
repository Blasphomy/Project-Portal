package com.project.userprogress.models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
public class SkillTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    private String description;

    @ElementCollection
    @CollectionTable(name = "skill_tree_quests", joinColumns = @JoinColumn(name = "skill_tree_id"))
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<QuestNode> quests;

    @ElementCollection
    @CollectionTable(name = "skill_tree_dependencies", joinColumns = @JoinColumn(name = "skill_tree_id"))
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<QuestEdge> dependencies;

    // Getters and setters
}

@Embeddable
record QuestNode(
    String id, // e.g., "quest_1", "quest_2"
    String title,
    String description,
    String category // e.g., "Java Basics", "Spring Boot", "REST APIs"
) {}

@Embeddable
record QuestEdge(
    String from, // The ID of the prerequisite quest
    String to    // The ID of the quest that depends on the 'from' quest
) {}
