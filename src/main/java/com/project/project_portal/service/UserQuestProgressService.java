package com.project.project_portal.service;

import com.project.project_portal.dto.UserQuestProgress;
import com.project.project_portal.repo.UserQuestProgressRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * UserQuestProgressService manages quest completion tracking for users.
 *
 * IMPORTANT: This service is kept for direct access to quest progress records,
 * but all game logic updates should go through ProgressDomainService which
 * handles completion detection, task validation, and badge awards atomically.
 */
@Service
public class UserQuestProgressService {

    private final UserQuestProgressRepository repository;

    public UserQuestProgressService(UserQuestProgressRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves quest progress for a specific user and quest combination.
     *
     * @param userId The user ID
     * @param questId The quest ID
     * @return Mono<UserQuestProgress> or empty if not found
     */
    public Mono<UserQuestProgress> getByUserAndQuest(String userId, String questId) {
        return repository.findByUserIdAndQuestId(userId, questId);
    }

    /**
     * Retrieves all quest progress records for a user.
     *
     * @param userId The user ID
     * @return Flux<UserQuestProgress> of all quests the user has started
     */
    public Flux<UserQuestProgress> getByUser(String userId) {
        return repository.findByUserId(userId);
    }

    /**
     * Saves or updates a quest progress record.
     *
     * @param progress The quest progress to save
     * @return Mono<UserQuestProgress> with persisted data
     */
    public Mono<UserQuestProgress> save(UserQuestProgress progress) {
        return repository.save(progress);
    }

    /**
     * Updates quest progress for a user.
     * Merges new progress data with existing record.
     *
     * CAUTION: Direct update bypasses game logic in ProgressDomainService.
     * Use ProgressDomainService.updateQuestProgressOnComplete() for game flow updates.
     *
     * @param userId The user ID
     * @param questId The quest ID
     * @param progress The updated progress data
     * @return Mono<UserQuestProgress> or empty if record not found
     */
    public Mono<UserQuestProgress> updateProgress(String userId, String questId, UserQuestProgress progress) {
        return repository.findByUserIdAndQuestId(userId, questId)
                .flatMap(existing -> {
                    existing.setStatus(progress.getStatus());
                    existing.setGainedXp(progress.getGainedXp());
                    return repository.save(existing);
                });
    }
}