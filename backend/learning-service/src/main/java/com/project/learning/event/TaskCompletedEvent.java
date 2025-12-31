package com.project.learning.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a task is completed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletedEvent {
    private String userId;
    private String taskId;
    private String questId;
    private String topicId;
    private Integer xpEarned;
    private LocalDateTime completedAt;
}
