package com.project.project_portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Getter
@Setter
@Table("topics")
public class Topic {

    @Id
    private String id;
    @NotBlank(message = "Topic name is required")
    private String name;
    private String description;

    public Topic() {
    }

    public Topic(String name, String description) {
        this.name = name;
        this.description = description;
    }
}