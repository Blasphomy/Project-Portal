package com.project.learning.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("quests")
public class Quest {
    @Id
    private String id;
    private String topicId;
    private String name;
    private String description;
    private Integer sequenceOrder;
    private String difficulty;
    private Integer estimatedMinutes;
    private String learningObjectives; // JSON array
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

