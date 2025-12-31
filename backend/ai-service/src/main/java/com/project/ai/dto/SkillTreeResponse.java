package com.project.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SkillTreeResponse(
    String title,
    String description,
    List<QuestNode> quests,
    List<QuestEdge> dependencies,
    Long userId
) {
    public SkillTreeResponse(String title, String description, List<QuestNode> quests, List<QuestEdge> dependencies) {
        this(title, description, quests, dependencies, null);
    }
}

record QuestNode(
    String id,
    String title,
    String description,
    String category
) {}

record QuestEdge(
    String from,
    String to
) {}
