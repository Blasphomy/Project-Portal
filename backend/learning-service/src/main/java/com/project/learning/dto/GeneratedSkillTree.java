package com.project.learning.dto;

import java.util.List;

public class GeneratedSkillTree {
    private String title;
    private String description;
    private String userGoal; // Store the original user goal
    private List<Quest> nodes;
    private List<Dependency> edges;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserGoal() {
        return userGoal;
    }

    public void setUserGoal(String userGoal) {
        this.userGoal = userGoal;
    }

    public List<Quest> getNodes() {
        return nodes;
    }

    public void setNodes(List<Quest> nodes) {
        this.nodes = nodes;
    }

    public List<Dependency> getEdges() {
        return edges;
    }

    public void setEdges(List<Dependency> edges) {
        this.edges = edges;
    }

    public static class Quest {
        private String id;
        private String title;
        private String description;
        private String category;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public static class Dependency {
        private String source;
        private String target;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }
}
