package com.project.project_portal.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Table("user_task_progress")
public class UserTaskProgress {

    @Id
    private String id;
    private String userId;
    private String taskId;
    private String status;
    private Integer gainedXp;
    private LocalDateTime updatedAt;

    public UserTaskProgress() {
    }

    public UserTaskProgress(String userId, String taskId, String status, Integer gainedXp, LocalDateTime completedAt) {
        this.userId = userId;
        this.taskId = taskId;
        this.status = status;
        this.gainedXp = gainedXp;
        this.updatedAt = completedAt;
    }

}