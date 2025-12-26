package com.project.project_portal.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;
@Getter
@Setter
@Table("user_quest_progress")
public class UserQuestProgress {

    @Id
    private String id;
    private String userId;
    private String questId;
    private String status;
    private Integer gainedXp;

    public UserQuestProgress() {
    }

    public UserQuestProgress(String userId, String questId, String status, Integer gainedXp) {
        this.userId = userId;
        this.questId = questId;
        this.status = status;
        this.gainedXp = gainedXp;
    }
}