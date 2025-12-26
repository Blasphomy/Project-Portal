package com.project.project_portal.service;

import com.project.project_portal.dto.UserTaskProgress;
import com.project.project_portal.repo.UserTaskProgressRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * UserTaskProgressService manages individual task completion tracking for users.
 *
 * IMPORTANT: This service is kept for direct access to task progress records,
 * but all game logic updates should go through ProgressDomainService which
 * handles XP, quest updates, and badge awards atomically.
 */
@Service
public class UserTaskProgressService {

    private final UserTaskProgressRepository repository;

    public UserTaskProgressService(UserTaskProgressRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves task progress for a specific user and task combination.
     *
     * @param userId The user ID
     * @param taskId The task ID
     * @return Mono<UserTaskProgress> or empty if not found
     */
    public Mono<UserTaskProgress> getByUserAndTask(String userId, String taskId) {
        return repository.findByUserIdAndTaskId(userId, taskId);
    }

    /**
     * Retrieves all task progress records for a user.
     *
     * @param userId The user ID
     * @return Flux<UserTaskProgress> of all tasks the user has interacted with
     */
    public Flux<UserTaskProgress> getByUser(String userId) {
        return repository.findByUserId(userId);
    }

    /**
     * Saves or updates a task progress record.
     *
     * @param progress The task progress to save
     * @return Mono<UserTaskProgress> with persisted data
     */
    public Mono<UserTaskProgress> save(UserTaskProgress progress) {
        return repository.save(progress);
    }

    /**
     * Updates task progress for a user.
     * Merges new progress data with existing record.
     *
     * CAUTION: Direct update bypasses game logic in ProgressDomainService.
     * Use ProgressDomainService.completeTask() for game flow updates.
     *
     * @param userId The user ID
     * @param taskId The task ID
     * @param progress The updated progress data
     * @return Mono<UserTaskProgress> or empty if record not found
     */
    public Mono<UserTaskProgress> updateProgress(String userId, String taskId, UserTaskProgress progress) {
        return repository.findByUserIdAndTaskId(userId, taskId)
                .flatMap(existing -> {
                    existing.setStatus(progress.getStatus());
                    existing.setGainedXp(progress.getGainedXp());
                    existing.setUpdatedAt(progress.getUpdatedAt());
                    return repository.save(existing);
                });
    }
}