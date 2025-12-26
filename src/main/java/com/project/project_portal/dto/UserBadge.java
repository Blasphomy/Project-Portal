package com.project.project_portal.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;
@Setter
@Getter
@Table("user_badges")
public class UserBadge {

    @Id
    private String id;

    private String userId;
    private String badgeId;
    private LocalDateTime earnedAt;

    public UserBadge() {}

    public UserBadge(String userId, String badgeId, LocalDateTime earnedAt) {
        this.userId = userId;
        this.badgeId = badgeId;
        this.earnedAt = earnedAt;
    }

}