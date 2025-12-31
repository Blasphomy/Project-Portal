import apiClient from './api';

// Learning service - handles all learning content API calls
export const learningService = {
    // Get all topics
    async getTopics() {
        return apiClient.get('/api/topics');
    },

    // Get quests by topic ID
    async getQuestsByTopic(topicId) {
        return apiClient.get(`/api/topics/${topicId}/quests`);
    },

    // Get specific quest details
    async getQuest(questId) {
        return apiClient.get(`/api/quests/${questId}`);
    },

    // Get tasks for a quest
    async getTasksByQuest(questId) {
        return apiClient.get(`/api/quests/${questId}/tasks`);
    },

    // Get specific task details
    async getTask(taskId) {
        return apiClient.get(`/api/tasks/${taskId}`);
    },

    // Submit task completion
    async submitTask(taskId, code) {
        return apiClient.post(`/api/tasks/${taskId}/submit`, {
            code,
            language: 'java', // Default to Java for now
        });
    },

    // Get user progress
    async getUserProgress(userId) {
        return apiClient.get(`/api/progress/user/${userId}`);
    },
};

export default learningService;
