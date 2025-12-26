package com.project.project_portal.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Getter
@Setter
@Table("badges")
public class Badge {

    @Id
    private String id;
    private String name;
    private String description;
    private String iconUrl;

    public Badge() {}

    public Badge(String name, String description, String iconUrl) {
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }
}