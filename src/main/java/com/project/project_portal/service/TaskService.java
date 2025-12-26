package com.project.project_portal.service;

import com.project.project_portal.dto.Task;
import com.project.project_portal.repo.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * TaskService handles task management operations including:
 * - Retrieving tasks by ID or quest
 * - Creating, updating, and deleting tasks
 *
 * A task is the smallest learning unit that users can complete.
 * Tasks are associated with quests and carry XP rewards.
 */
@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all tasks in the system.
     *
     * @return Flux<Task> of all tasks
     */
    public Flux<Task> getAllTasks() {
        return repository.findAll();
    }

    /**
     * Retrieves a specific task by ID.
     *
     * @param id The task ID
     * @return Mono<Task> or empty if not found
     */
    public Mono<Task> getTaskById(String id) {
        return repository.findById(id);
    }

    /**
     * Retrieves all tasks for a specific quest, ordered by their sequence.
     *
     * @param questId The quest ID
     * @return Flux<Task> ordered by orderIndex
     */
    public Flux<Task> getTasksByQuestId(String questId) {
        return repository.findByQuestIdOrderByOrderIndexAsc(questId);
    }

    /**
     * Creates a new task.
     *
     * @param task The task to create
     * @return Mono<Task> with persisted data
     */
    public Mono<Task> createTask(Task task) {
        return repository.save(task);
    }

    /**
     * Updates an existing task.
     * Merges updated fields with existing task data.
     *
     * @param id The task ID to update
     * @param task The updated task data
     * @return Mono<Task> with merged data or empty if task not found
     */
    public Mono<Task> updateTask(String id, Task task) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setTitle(task.getTitle());
                    existing.setDescription(task.getDescription());
                    existing.setQuestId(task.getQuestId());
                    existing.setXpReward(task.getXpReward());
                    existing.setOrderIndex(task.getOrderIndex());
                    return repository.save(existing);
                });
    }

    /**
     * Deletes a task.
     *
     * @param id The task ID to delete
     * @return Mono<Void>
     */
    public Mono<Void> deleteTask(String id) {
        return repository.deleteById(id);
    }
}