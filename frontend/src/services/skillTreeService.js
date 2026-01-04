import apiClient from './api';

// Skill Tree service - handles all skill tree and quest management API calls
export const skillTreeService = {
    /**
     * Get the user's active skill tree with nodes and dependencies
     */
    async getUserSkillTree(userId) {
        return apiClient.get(`/api/skill-trees/user/${userId}`);
    },

    /**
     * Mark a quest as completed (triggers unlocking of dependent quests)
     */
    async completeQuest(skillTreeId, questId, userId) {
        return apiClient.post(
            `/api/skill-trees/${skillTreeId}/quests/${questId}/complete`,
            {},
            {
                headers: {
                    'X-User-Id': userId,
                }
            }
        );
    },

    /**
     * Get all currently unlocked quests for a skill tree
     */
    async getUnlockedQuests(skillTreeId) {
        return apiClient.get(`/api/skill-trees/${skillTreeId}/unlocked-quests`);
    },

    /**
     * Create a new skill tree from AI-generated data
     */
    async createSkillTree(generatedSkillTree, userId) {
        return apiClient.post(
            '/api/skill-trees',
            generatedSkillTree,
            {
                headers: {
                    'X-User-Id': userId,
                }
            }
        );
    },
};

export default skillTreeService;
