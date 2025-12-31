
package com.project.ai.dto.learningservice;

// Using records for immutable, concise DTOs
public record Edge(
    String source, // Renamed from 'from' to match our entity
    String target  // Renamed from 'to' to match our entity
) {}
