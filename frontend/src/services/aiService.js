import apiClient from './api';

// AI service - handles all AI-related API calls
export const aiService = {
    // Generate AI content from prompt
    async generateContent(prompt) {
        return apiClient.post('/api/ai/generate', {
            prompt,
        });
    },

    // Get AI-generated study material for a quest
    async getStudyMaterial(topicName, questName, questDescription, tasks) {
        const prompt = `Generate comprehensive study material for learning ${topicName}. 
Quest: ${questName}
Description: ${questDescription}
Tasks: ${tasks.join(', ')}

Provide the response in JSON format with: introduction, concepts, examples, commonMistakes, exercises, and resources.`;

        return this.generateContent(prompt);
    },

    // Get AI hint for a coding task
    async getHint(taskTitle, taskDescription, userCode, language = 'java') {
        const prompt = `A programming beginner is working on: ${taskTitle}
Description: ${taskDescription}
Language: ${language}

Their current code:
\`\`\`
${userCode}
\`\`\`

Provide 3 progressive hints in JSON format with different levels: concept, syntax, and logic.`;

        return this.generateContent(prompt);
    },

    // Get AI code review
    async reviewCode(taskTitle, taskDescription, userCode, language = 'java') {
        const prompt = `Review this ${language} code for a beginner programmer.

Task: ${taskTitle}
Description: ${taskDescription}

Code submitted:
\`\`\`
${userCode}
\`\`\`

Provide review in JSON format with: overallAssessment, score, feedback array, strengths, improvements, and passesTests boolean.`;

        return this.generateContent(prompt);
    },

    // General AI chat
    async chat(message) {
        return this.generateContent(message);
    },
};

export default aiService;
