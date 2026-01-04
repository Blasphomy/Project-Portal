package com.project.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SkillTreeResponse(
                String title,
                String description,
                List<QuestNode> nodes, // Changed from "quests" to "nodes"
                List<QuestEdge> edges, // Changed from "dependencies" to "edges"
                String userGoal, // Added userGoal
                String userId) {
        public SkillTreeResponse(String title, String description, List<QuestNode> nodes, List<QuestEdge> edges,
                        String userGoal) {
                this(title, description, nodes, edges, userGoal, null);
        }
}

record QuestNode(
                String id,
                String title,
                String description,
                String category) {
}

record QuestEdge(
                String source, // Changed from "from" to "source"
                String target) { // Changed from "to" to "target"
}
