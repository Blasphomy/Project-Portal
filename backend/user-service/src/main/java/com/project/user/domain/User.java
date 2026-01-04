package com.project.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    private String id;
    @Column(value = "username")
    private String username;
    @Column(value = "full_name")
    private String fullName;
    @Column(value = "email")
    private String email;
    @Column(value = "password_hash")
    private String password;
    @Column(value = "avatar_url")
    private String avatarUrl;
    @Column(value = "total_xp")
    private Integer totalXp;
    private Integer level;
    @Column(value = "is_active")
    private Boolean isActive;
    @Column(value = "is_email_verified")
    private Boolean isEmailVerified;
    @Column(value = "oauth_provider")
    private String oauthProvider; // google, github, null for local
    @Column(value = "oauth_id")
    private String oauthId;
    @Column(value = "created_at")
    private LocalDateTime createdAt;
    @Column(value = "updated_at")
    private LocalDateTime updatedAt;
    @Column(value = "last_login_at")
    private LocalDateTime lastLoginAt;

    @org.springframework.data.annotation.Transient
    private Role role;

    public enum Role {
        USER, ADMIN
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // email is the username
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive == null || this.isActive;
    }
}
