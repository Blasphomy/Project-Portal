package com.project.project_portal.service;

import com.project.project_portal.dto.Badge;
import com.project.project_portal.dto.UserBadge;
import com.project.project_portal.repo.BadgeRepository;
import com.project.project_portal.repo.UserBadgeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BadgeService manages badge-related operations including:
 * - Retrieving available badges
 * - Awarding badges to users
 * - Fetching user's earned badges
 *
 * This service ensures badge validity before awarding and prevents
 * duplicate badge awards through unique constraint handling.
 */
@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public BadgeService(BadgeRepository badgeRepository, UserBadgeRepository userBadgeRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    /**
     * Retrieves all available badges in the system.
     *
     * @return Flux<Badge> of all badges
     */
    public Flux<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    /**
     * Retrieves a specific badge by its ID.
     *
     * @param id The badge ID
     * @return Mono<Badge> or empty if not found
     */
    public Mono<Badge> getBadgeById(String id) {
        return badgeRepository.findById(id);
    }

    /**
     * Creates a new badge with auto-generated ID if not provided.
     *
     * @param badge The badge to create
     * @return Mono<Badge> with persisted data
     */
    public Mono<Badge> createBadge(Badge badge) {
        if (badge.getId() == null) {
            badge.setId(UUID.randomUUID().toString());
        }
        return badgeRepository.save(badge);
    }

    /**
     * Retrieves all badges earned by a specific user.
     *
     * @param userId The user ID
     * @return Flux<Badge> of all badges the user has earned
     */
    public Flux<Badge> getUserBadges(String userId) {
        return userBadgeRepository.findByUserId(userId)
                .flatMap(ub -> badgeRepository.findById(ub.getBadgeId()));
    }

    /**
     * Awards a badge to a user if they don't already have it.
     * Validates badge exists before awarding.
     * Uses database UNIQUE constraint to prevent duplicates.
     *
     * @param userId The user to award badge to
     * @param badgeId The badge ID to award
     * @return Mono<UserBadge> with award details
     * @throws IllegalArgumentException if badge doesn't exist
     */
    public Mono<UserBadge> awardBadgeToUser(String userId, String badgeId) {
        // First verify badge exists to catch invalid badge IDs early
        return badgeRepository.findById(badgeId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Badge not found: " + badgeId)))
                .flatMap(badge ->
                    userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId)
                            .switchIfEmpty(
                                Mono.defer(() -> {
                                    UserBadge userBadge = new UserBadge(
                                        userId,
                                        badgeId,
                                        LocalDateTime.now()
                                    );
                                    return userBadgeRepository.save(userBadge);
                                })
                            )
                );
    }

    /**
     * Removes a badge from a user.
     *
     * @param userId The user ID
     * @param badgeId The badge ID to remove
     * @return Mono<Void>
     */
    public Mono<Void> removeBadgeFromUser(String userId, String badgeId) {
        return userBadgeRepository.deleteByUserIdAndBadgeId(userId, badgeId);
    }

    /**
     * Attempts to award a badge to a user, gracefully handling errors.
     * If badge doesn't exist or award fails, logs warning and continues.
     *
     * @param userId The user ID
     * @param badgeId The badge ID to award
     * @return Mono<Void> that completes successfully even if award fails
     */
    public Mono<Void> awardBadgeIfEarned(String userId, String badgeId) {
        return awardBadgeToUser(userId, badgeId)
                .onErrorResume(e -> {
                    System.err.println("Warning: Could not award badge " + badgeId + " to user " + userId + ": " + e.getMessage());
                    return Mono.empty();
                })
                .then();
    }

    /**
     * DEPRECATED: Badge awards are now handled by ProgressDomainService with milestone-based logic.
     * This method uses hardcoded badge IDs that may not exist in the database.
     * Kept for backward compatibility but should not be used.
     *
     * @param userId The user ID
     * @param totalTasksCompleted Total tasks completed
     * @param totalQuestsCompleted Total quests completed
     * @return Mono<Void>
     */
    public Mono<Void> checkAndAwardCompletionBadges(String userId, int totalTasksCompleted, int totalQuestsCompleted) {
        Mono<Void> tasksCheck = Mono.empty();
        Mono<Void> questsCheck = Mono.empty();

        // Award badge for 5 completed tasks
        if (totalTasksCompleted == 5) {
            tasksCheck = awardBadgeToUser(userId, "badge-task-warrior").then();
        }

        // Award badge for 10 completed tasks
        if (totalTasksCompleted == 10) {
            tasksCheck = awardBadgeToUser(userId, "badge-task-legend").then();
        }

        // Award badge for completing first quest
        if (totalQuestsCompleted == 1) {
            questsCheck = awardBadgeToUser(userId, "badge-quest-starter").then();
        }

        // Award badge for completing 3 quests
        if (totalQuestsCompleted == 3) {
            questsCheck = awardBadgeToUser(userId, "badge-quest-explorer").then();
        }

        return tasksCheck.then(questsCheck);
    }

    /**
     * DEPRECATED: Task count is now tracked through ProgressDomainService.
     * This method always returns 0.
     * The actual task progress tracking is done in ProgressDomainService.getAllUserTaskProgress()
     *
     * @param userId The user ID
     * @return Mono<Integer> always returns 0
     */
    public Mono<Integer> getCompletedTaskCount(String userId) {
        // This would require access to TaskProgressRepository
        // For now, return 0; wire this up in ProgressDomainService
        return Mono.just(0);
    }

    /**
     * DEPRECATED: Quest count is now tracked through ProgressDomainService.
     * This method always returns 0.
     * The actual quest progress tracking is done in ProgressDomainService.getAllUserQuestProgress()
     *
     * @param userId The user ID
     * @return Mono<Integer> always returns 0
     */
    public Mono<Integer> getCompletedQuestCount(String userId) {
        // This would require access to QuestProgressRepository
        // For now, return 0; wire this up in ProgressDomainService
        return Mono.just(0);
    }
}