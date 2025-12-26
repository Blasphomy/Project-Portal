package com.project.project_portal.dto;

import java.util.List;

public class TopicTreeView {

    public static class TaskView {
        private String id;
        private String title;
        private String content;
        private Integer orderIndex;
        private Integer xpReward;

        public TaskView() {
        }

        public TaskView(String id, String title, String content, Integer orderIndex, Integer xpReward) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.orderIndex = orderIndex;
            this.xpReward = xpReward;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public Integer getOrderIndex() {
            return orderIndex;
        }

        public Integer getXpReward() {
            return xpReward;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setOrderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
        }

        public void setXpReward(Integer xpReward) {
            this.xpReward = xpReward;
        }
    }

    public static class QuestView {
        private String id;
        private String title;
        private String description;
        private Integer orderIndex;
        private List<TaskView> tasks;

        public QuestView() {
        }

        public QuestView(String id, String title, String description, Integer orderIndex, List<TaskView> tasks) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.orderIndex = orderIndex;
            this.tasks = tasks;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Integer getOrderIndex() {
            return orderIndex;
        }

        public List<TaskView> getTasks() {
            return tasks;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setOrderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
        }

        public void setTasks(List<TaskView> tasks) {
            this.tasks = tasks;
        }
    }

    private String id;
    private String title;
    private String description;
    private List<QuestView> quests;

    public TopicTreeView() {
    }

    public TopicTreeView(String id, String title, String description, List<QuestView> quests) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.quests = quests;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<QuestView> getQuests() {
        return quests;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuests(List<QuestView> quests) {
        this.quests = quests;
    }
}