package com.project.project_portal.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Getter
@Setter
@Table("quests")
public class Quest {

    @Id
    private String id;
    private String topicId;
    private String name;
    private String description;
    private Integer orderIndex;

    public Quest() {
    }

    public Quest(String topicId, String name, String description, Integer orderIndex) {
        this.topicId = topicId;
        this.name = name;
        this.description = description;
        this.orderIndex = orderIndex;
    }
}
