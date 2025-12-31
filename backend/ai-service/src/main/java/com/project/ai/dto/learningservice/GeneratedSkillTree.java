
package com.project.ai.dto.learningservice;

import java.util.List;

// Using records for immutable, concise DTOs
public record GeneratedSkillTree(
    String title,
    String description,
    List<Node> nodes, // Renamed from 'quests' for clarity
    List<Edge> edges  // Renamed from 'dependencies' for clarity
) {}
