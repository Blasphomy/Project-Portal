package com.project.learning.service;

import com.project.common.exception.ResourceNotFoundException;
import com.project.learning.domain.Task;
import com.project.learning.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Flux<Task> getTasksByQuestId(String questId) {
        log.debug("Fetching tasks for quest: {}", questId);
        return taskRepository.findByQuestIdOrderBySequenceOrder(questId);
    }

    public Mono<Task> getTaskById(String id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task", id)));
    }

    public Mono<Task> createTask(Task task) {
        return taskRepository.save(task);
    }
}
