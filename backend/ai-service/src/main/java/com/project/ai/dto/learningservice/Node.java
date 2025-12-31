
package com.project.ai.dto.learningservice;

// Using records for immutable, concise DTOs
public record Node(
    String id,
    String title,
    String description,
    String category
) {}
