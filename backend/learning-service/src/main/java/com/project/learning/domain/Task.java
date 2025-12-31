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
@Table("tasks")
public class Task {
    @Id
    private String id;
    private String questId;
    private String title;
    private String description;
    private Integer xpReward;
    private Integer sequenceOrder;
    private Boolean hasCodeChallenge;
    private String starterCode;
    private String solutionCode;
    private String testCases; // JSON array
    private String hints; // JSON array
    private String difficulty;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

