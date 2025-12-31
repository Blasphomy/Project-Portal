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
@Table("topics")
public class Topic {
    @Id
    private String id;
    private String name;
    private String description;
    private String iconUrl;
    private String difficultyLevel;
    private Integer estimatedHours;
    private String prerequisites; // JSON array
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
