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
@Table("user_progress")
public class UserProgress {
    @Id
    private String id;
    private String userId;
    private String taskId;
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED
    private String codeSubmission;
    private Integer xpEarned;
    private Integer attempts;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

