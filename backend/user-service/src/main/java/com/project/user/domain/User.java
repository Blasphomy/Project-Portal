package com.project.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User implements Persistable<String> {
    @Id
    private String id;
    private String username;
    private String email;
    @Column("password_hash")
    private String passwordHash;
    @Column("full_name")
    private String fullName;
    @Column("avatar_url")
    private String avatarUrl;
    @Column("total_xp")
    private Integer totalXp;
    private Integer level;
    @Column("is_active")
    private Boolean isActive;
    @Column("is_email_verified")
    private Boolean isEmailVerified;
    @Column("oauth_provider")
    private String oauthProvider; // google, github, null for local
    @Column("oauth_id")
    private String oauthId;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("last_login_at")
    private LocalDateTime lastLoginAt;

    @Override
    public boolean isNew() {
        // If createdAt is null, it's a new entity about to be inserted
        return createdAt == null;
    }
}
