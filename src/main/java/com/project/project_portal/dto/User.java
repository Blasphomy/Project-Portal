
package com.project.project_portal.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Getter
@Setter
@Table("users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private Integer totalXp;

    public User() {
    }

    public User(String name, String email, Integer totalXp) {
        this.name = name;
        this.email = email;
        this.totalXp = totalXp;
    }
}