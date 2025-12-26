package com.project.project_portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Getter
@Setter
@Table("tasks")
public class Task {

    @Id
    private String id;
    @NotBlank(message = "Quest ID is required")
    private String questId;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @Positive(message = "XP reward must be positive")
    private Integer xpReward;
    private Integer orderIndex;

    public Task() {
    }

    public Task(String questId, String title, String description, Integer xpReward, Integer orderIndex) {
        this.questId = questId;
        this.title = title;
        this.description = description;
        this.xpReward = xpReward;
        this.orderIndex = orderIndex;
    }
}