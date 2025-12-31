package com.project.learning.controller;

import com.project.learning.domain.Quest;
import com.project.learning.domain.Task;
import com.project.learning.service.QuestService;
import com.project.learning.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LearningController {

    private final QuestService questService;
    private final TaskService taskService;

    @GetMapping("/topics/{topicId}/quests")
    public Flux<Quest> getQuestsByTopic(@PathVariable String topicId) {
        return questService.getQuestsByTopicId(topicId);
    }

    @GetMapping("/quests/{questId}")
    public Mono<Quest> getQuest(@PathVariable String questId) {
        return questService.getQuestById(questId);
    }

    @GetMapping("/quests/{questId}/tasks")
    public Flux<Task> getTasksByQuest(@PathVariable String questId) {
        return taskService.getTasksByQuestId(questId);
    }

    @GetMapping("/tasks/{taskId}")
    public Mono<Task> getTask(@PathVariable String taskId) {
        return taskService.getTaskById(taskId);
    }
}
