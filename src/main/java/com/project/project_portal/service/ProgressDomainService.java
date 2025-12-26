package com.project.project_portal.service;

import com.project.project_portal.dto.UserQuestProgress;
import com.project.project_portal.dto.UserTaskProgress;
import com.project.project_portal.dto.Task;
import com.project.project_portal.dto.User;
import com.project.project_portal.repo.UserTaskProgressRepository;
import com.project.project_portal.repo.UserQuestProgressRepository;
import com.project.project_portal.repo.UserRepository;
import com.project.project_portal.repo.TaskRepository;
import com.project.project_portal.repo.QuestRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ProgressDomainService manages the game progression logic including:
 * - Task completion tracking
 * - Quest progress updates
 * - User XP management
 * - Badge awarding for milestones
 *
 * This service coordinates between multiple repositories to maintain
 * game state consistency when users interact with tasks and quests.
 */
@Service
public class ProgressDomainService {

    // Constants for badge IDs
    private static final String BADGE_FIRST_STEP = "badge-1";
    private static final String BADGE_TASK_WARRIOR = "badge-3";
    private static final String BADGE_TASK_LEGEND = "badge-4";
    private static final String BADGE_QUEST_STARTER = "badge-5";
    private static final String BADGE_QUEST_EXPLORER = "badge-6";
    private static final String BADGE_QUEST_GOD = "badge-7";
    private static final String BADGE_LEGEND_MASTER = "badge-8";
    private static final String BADGE_JAVA_MASTER = "badge-9";

    // Constants for progress statuses
    private static final String STATUS_NOT_STARTED = "NOT_STARTED";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";

    // Constants for milestone counts
    private static final int MILESTONE_FIRST_TASK = 1;
    private static final int MILESTONE_FIVE_TASKS = 5;
    private static final int MILESTONE_TEN_TASKS = 10;
    private static final int MILESTONE_FIRST_QUEST = 1;
    private static final int MILESTONE_THREE_QUESTS = 3;
    private static final int MILESTONE_ALL_QUESTS = 2; // Based on seed data having 2 quests

    private final UserTaskProgressRepository taskProgressRepo;
    private final UserQuestProgressRepository questProgressRepo;
    private final UserRepository userRepo;
    private final TaskRepository taskRepo;
    private final QuestRepository questRepo;
    private final BadgeService badgeService;

    public ProgressDomainService(
            UserTaskProgressRepository taskProgressRepo,
            UserQuestProgressRepository questProgressRepo,
            UserRepository userRepo,
            TaskRepository taskRepo,
            QuestRepository questRepo,
            BadgeService badgeService) {
        this.taskProgressRepo = taskProgressRepo;
        this.questProgressRepo = questProgressRepo;
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
        this.questRepo = questRepo;
        this.badgeService = badgeService;
    }

    /**
     * Starts or resumes a task for a user.
     * Creates a quest progress record if this is the first task in a quest.
     *
     * @param userId The user attempting to start the task
     * @param taskId The task to start
     * @return Mono<UserTaskProgress> with status IN_PROGRESS
     */
    public Mono<UserTaskProgress> startTask(String userId, String taskId) {
        return taskRepo.findById(taskId)
                .switchIfEmpty(Mono.error(new IllegalStateException("Task not found")))
                .flatMap(task -> ensureQuestProgressOnStart(userId, task)
                        .then(taskProgressRepo.findByUserIdAndTaskId(userId, taskId))
                        .flatMap(existing -> {
                            // If already completed or in progress, just return it
                            if (STATUS_COMPLETED.equals(existing.getStatus()) ||
                                STATUS_IN_PROGRESS.equals(existing.getStatus())) {
                                return Mono.just(existing);
                            }
                            // Any other status: set to IN_PROGRESS and update
                            existing.setStatus(STATUS_IN_PROGRESS);
                            existing.setUpdatedAt(LocalDateTime.now());
                            return taskProgressRepo.save(existing);
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            // No row for this user/task: create a fresh one
                            UserTaskProgress progress = new UserTaskProgress(
                                    userId,
                                    taskId,
                                    STATUS_IN_PROGRESS,
                                    0,
                                    LocalDateTime.now()
                            );
                            return taskProgressRepo.save(progress);
                        }))
                );
    }

    /**
     * Ensures a quest progress record exists when a user starts a task.
     * If the quest progress doesn't exist, creates one with IN_PROGRESS status.
     *
     * @param userId The user ID
     * @param task The task being started
     * @return Mono<UserQuestProgress> or empty Mono if task has no quest
     */
    private Mono<UserQuestProgress> ensureQuestProgressOnStart(String userId, Task task) {
        String questId = task.getQuestId();
        if (questId == null) {
            return Mono.empty();
        }

        return questProgressRepo.findByUserIdAndQuestId(userId, questId)
                .switchIfEmpty(Mono.defer(() -> {
                    UserQuestProgress qp = new UserQuestProgress();
                    qp.setUserId(userId);
                    qp.setQuestId(questId);
                    qp.setStatus(STATUS_IN_PROGRESS);
                    qp.setGainedXp(0);
                    return questProgressRepo.save(qp);
                }));
    }

    /**
     * Marks a task as completed for a user.
     * Updates user XP, quest progress, and awards relevant badges.
     *
     * @param userId The user completing the task
     * @param taskId The task to complete
     * @return Mono<UserTaskProgress> with completed status
     */
    public Mono<UserTaskProgress> completeTask(String userId, String taskId) {
        return taskProgressRepo.findByUserIdAndTaskId(userId, taskId)
                .switchIfEmpty(Mono.error(new IllegalStateException("Task not started")))
                .flatMap(progress -> {
                    if (progress.getId() == null) {
                        return Mono.error(new IllegalStateException("Task progress has null id; recreate progress data"));
                    }

                    if (STATUS_COMPLETED.equals(progress.getStatus())) {
                        return Mono.just(progress);
                    }

                    return taskRepo.findById(taskId)
                            .switchIfEmpty(Mono.error(new IllegalStateException("Task not found")))
                            .flatMap(task -> {
                                int xpReward = task.getXpReward() == null ? 0 : task.getXpReward();

                                progress.setStatus(STATUS_COMPLETED);
                                progress.setGainedXp(xpReward);
                                progress.setUpdatedAt(LocalDateTime.now());

                                // Chain all side effects: save progress → update XP → update quest → award badges
                                return taskProgressRepo.save(progress)
                                        .flatMap(savedProgress ->
                                                updateUserXp(userId, xpReward)
                                                        .then(updateQuestProgressOnComplete(userId, task, xpReward))
                                                        .then(awardDynamicTaskBadges(userId))
                                                        .thenReturn(savedProgress)
                                        );
                            });
                });
    }

    /**
     * Updates quest progress when a task is completed.
     * Accumulates XP for the quest and marks quest as COMPLETED if all tasks are done.
     *
     * @param userId The user ID
     * @param task The completed task
     * @param xpReward The XP gained from the task
     * @return Mono<UserQuestProgress> with updated progress
     */
    private Mono<UserQuestProgress> updateQuestProgressOnComplete(String userId, Task task, int xpReward) {
        String questId = task.getQuestId();
        if (questId == null) {
            return Mono.empty();
        }

        return questProgressRepo.findByUserIdAndQuestId(userId, questId)
                .switchIfEmpty(Mono.defer(() -> {
                    UserQuestProgress qp = new UserQuestProgress();
                    qp.setUserId(userId);
                    qp.setQuestId(questId);
                    qp.setStatus(STATUS_IN_PROGRESS);
                    qp.setGainedXp(xpReward);
                    return questProgressRepo.save(qp);
                }))
                .flatMap(qp -> {
                    int currentXp = qp.getGainedXp() == null ? 0 : qp.getGainedXp();
                    qp.setGainedXp(currentXp + xpReward);

                    // Check if all tasks in this quest are completed
                    return isAllQuestTasksCompleted(userId, questId)
                            .flatMap(allCompleted -> {
                                if (allCompleted) {
                                    qp.setStatus(STATUS_COMPLETED);
                                    return questProgressRepo.save(qp)
                                            .flatMap(saved -> awardQuestCompletionBadges(userId)
                                                    .thenReturn(saved));
                                } else {
                                    return questProgressRepo.save(qp);
                                }
                            });
                });
    }

    /**
     * Checks if all tasks in a quest have been completed by the user.
     *
     * @param userId The user ID
     * @param questId The quest ID
     * @return Mono<Boolean> true if all tasks are completed
     */
    private Mono<Boolean> isAllQuestTasksCompleted(String userId, String questId) {
        return taskRepo.findByQuestId(questId)
                .flatMap(task -> taskProgressRepo.findByUserIdAndTaskId(userId, task.getId())
                        .map(tp -> STATUS_COMPLETED.equals(tp.getStatus()))
                        .defaultIfEmpty(false)
                )
                .collectList()
                .map(results -> results.isEmpty() || results.stream().allMatch(b -> b));
    }

    /**
     * Updates the total XP for a user by adding the earned XP.
     *
     * @param userId The user ID
     * @param xpGain The amount of XP to add
     * @return Mono<Void>
     */
    private Mono<Void> updateUserXp(String userId, Integer xpGain) {
        return userRepo.findById(userId)
                .flatMap(user -> {
                    user.setTotalXp((user.getTotalXp() != null ? user.getTotalXp() : 0) + xpGain);
                    return userRepo.save(user);
                })
                .then();
    }

    /**
     * Awards task completion milestone badges based on the total number of completed tasks.
     * Checks milestones: 1st, 5th, and 10th task completion.
     *
     * @param userId The user ID
     * @return Mono<Void>
     */
    private Mono<Void> awardDynamicTaskBadges(String userId) {
        return taskProgressRepo.findByUserId(userId)
                .filter(tp -> STATUS_COMPLETED.equals(tp.getStatus()))
                .count()
                .flatMap(count -> {
                    java.util.List<Mono<Void>> badgeAwards = new java.util.ArrayList<>();

                    // Award badge for first task completion
                    if (count == MILESTONE_FIRST_TASK) {
                        badgeAwards.add(badgeService.awardBadgeToUser(userId, BADGE_FIRST_STEP)
                                .onErrorResume(e -> Mono.empty())
                                .then());
                    }
                    // Award badge for 5 tasks completed
                    if (count == MILESTONE_FIVE_TASKS) {
                        badgeAwards.add(badgeService.awardBadgeToUser(userId, BADGE_TASK_WARRIOR)
                                .onErrorResume(e -> Mono.empty())
                                .then());
                    }
                    // Award badge for 10 tasks completed
                    if (count == MILESTONE_TEN_TASKS) {
                        badgeAwards.add(badgeService.awardBadgeToUser(userId, BADGE_TASK_LEGEND)
                                .onErrorResume(e -> Mono.empty())
                                .then());
                    }

                    if (badgeAwards.isEmpty()) {
                        return Mono.empty();
                    }

                    return Mono.when(badgeAwards);
                });
    }

    /**
     * Awards quest completion milestone badges based on the total number of completed quests.
     * Checks milestones: 1st quest, 3 quests, and all quests (2 total).
     *
     * @param userId The user ID
     * @return Mono<Void>
     */
    private Mono<Void> awardQuestCompletionBadges(String userId) {
        return questProgressRepo.findByUserId(userId)
                .filter(qp -> STATUS_COMPLETED.equals(qp.getStatus()))
                .count()
                .flatMap(count -> {
                    java.util.List<Mono<Void>> badgeAwards = new java.util.ArrayList<>();

                    // Award badge for first quest completion
                    if (count == MILESTONE_FIRST_QUEST) {
                        badgeAwards.add(badgeService.awardBadgeToUser(userId, BADGE_QUEST_STARTER)
                                .onErrorResume(e -> Mono.empty())
                                .then());
                    }
                    // Award badge for completing 3 quests
                    if (count == MILESTONE_THREE_QUESTS) {
                        badgeAwards.add(badgeService.awardBadgeToUser(userId, BADGE_QUEST_EXPLORER)
                                .onErrorResume(e -> Mono.empty())
                                .then());
                    }
                    // Award badge for completing all quests
                    if (count == MILESTONE_ALL_QUESTS) {
                        badgeAwards.add(badgeService.awardBadgeToUser(userId, BADGE_QUEST_GOD)
                                .onErrorResume(e -> Mono.empty())
                                .then());
                    }

                    if (badgeAwards.isEmpty()) {
                        return Mono.empty();
                    }

                    return Mono.when(badgeAwards);
                });
    }

    /**
     * Retrieves a single task progress record for a user.
     *
     * @param userId The user ID
     * @param taskId The task ID
     * @return Mono<UserTaskProgress> or empty if not found
     */
    public Mono<UserTaskProgress> getUserTaskProgress(String userId, String taskId) {
        return taskProgressRepo.findByUserIdAndTaskId(userId, taskId);
    }

    /**
     * Retrieves a single quest progress record for a user.
     *
     * @param userId The user ID
     * @param questId The quest ID
     * @return Mono<UserQuestProgress> or empty if not found
     */
    public Mono<UserQuestProgress> getUserQuestProgress(String userId, String questId) {
        return questProgressRepo.findByUserIdAndQuestId(userId, questId);
    }

    /**
     * Retrieves all quest progress records for a user.
     *
     * @param userId The user ID
     * @return Flux<UserQuestProgress> of all quests the user has started
     */
    public Flux<UserQuestProgress> getAllUserQuestProgress(String userId) {
        return questProgressRepo.findByUserId(userId);
    }

    /**
     * Retrieves all task progress records for a user.
     *
     * @param userId The user ID
     * @return Flux<UserTaskProgress> of all tasks the user has interacted with
     */
    public Flux<UserTaskProgress> getAllUserTaskProgress(String userId) {
        return taskProgressRepo.findByUserId(userId);
    }

    /**
     * Provides a detailed view of a quest including all its tasks and their progress.
     * Returns a hierarchical structure with quest progress and individual task statuses.
     *
     * @param userId The user ID
     * @param questId The quest ID
     * @return Mono<Map<String, Object>> with quest and task details
     */
    public Mono<Map<String, Object>> getUserQuestWithTaskProgress(String userId, String questId) {
        return questProgressRepo.findByUserIdAndQuestId(userId, questId)
                .switchIfEmpty(Mono.error(new IllegalStateException("Quest not started")))
                .flatMap(questProgress -> {
                    // Get quest details
                    return Mono.just(questProgress);
                })
                .flatMap(questProgress ->
                        taskRepo.findByQuestId(questId)
                                .flatMap(task -> taskProgressRepo.findByUserIdAndTaskId(userId, task.getId())
                                        .map(taskProgress -> Map.of(
                                                "taskId", task.getId(),
                                                "taskTitle", task.getTitle(),
                                                "status", taskProgress.getStatus(),
                                                "gainedXp", taskProgress.getGainedXp()
                                        ))
                                        .switchIfEmpty(Mono.just(Map.of(
                                                "taskId", task.getId(),
                                                "taskTitle", task.getTitle(),
                                                "status", STATUS_NOT_STARTED,
                                                "gainedXp", 0
                                        )))
                                )
                                .collectList()
                                .map(taskList -> Map.of(
                                        "questId", questId,
                                        "questProgress", Map.of(
                                                "status", questProgress.getStatus(),
                                                "gainedXp", questProgress.getGainedXp()
                                        ),
                                        "tasks", taskList
                                ))
                );
    }

    /**
     * Retrieves comprehensive completion statistics for a user.
     * Includes task/quest counts, total XP, badges earned, and completion flags.
     *
     * @param userId The user ID
     * @return Mono<Map<String, Object>> with user completion statistics
     */
    public Mono<Map<String, Object>> getUserCompletionStatus(String userId) {
        return Mono.zip(
                taskProgressRepo.findByUserId(userId)
                        .filter(tp -> STATUS_COMPLETED.equals(tp.getStatus()))
                        .count(),
                questProgressRepo.findByUserId(userId)
                        .filter(qp -> STATUS_COMPLETED.equals(qp.getStatus()))
                        .count(),
                userRepo.findById(userId),
                badgeService.getUserBadges(userId).count()
        ).flatMap(tuple -> {
            long completedTasks = tuple.getT1();
            long completedQuests = tuple.getT2();
            User user = tuple.getT3();
            long badgesEarned = tuple.getT4();

            return taskRepo.findAll().count()
                    .flatMap(totalTasks ->
                            questRepo.findAll().count()
                                    .map(totalQuests -> {
                                        boolean isAllTasksCompleted = completedTasks == totalTasks && totalTasks > 0;
                                        boolean isAllQuestsCompleted = completedQuests == totalQuests && totalQuests > 0;

                                        Map<String, Object> status = new java.util.HashMap<>();
                                        status.put("userId", userId);
                                        status.put("totalXp", user.getTotalXp());
                                        status.put("tasksCompleted", completedTasks);
                                        status.put("tasksTotal", totalTasks);
                                        status.put("questsCompleted", completedQuests);
                                        status.put("questsTotal", totalQuests);
                                        status.put("badgesEarned", badgesEarned);
                                        status.put("allTasksCompleted", isAllTasksCompleted);
                                        status.put("allQuestsCompleted", isAllQuestsCompleted);
                                        status.put("isFullyCompleted", isAllTasksCompleted && isAllQuestsCompleted);

                                        return status;
                                    })
                    );
        });
    }

    /**
     * Awards ultimate mastery badges when a user completes all content.
     * Awards "Legend Master" for completing all tasks and quests.
     * Awards "Java Master" for completing all quests.
     *
     * @param userId The user ID
     * @return Mono<Void>
     */
    public Mono<Void> awardMasteryBadges(String userId) {
        return getUserCompletionStatus(userId)
                .flatMap(status -> {
                    java.util.List<Mono<Void>> badgeAwards = new java.util.ArrayList<>();

                    // Award ultimate mastery badge for completing everything
                    if ((boolean) status.get("isFullyCompleted")) {
                        badgeAwards.add(badgeService.awardBadgeToUser(userId, BADGE_LEGEND_MASTER)
                                .onErrorResume(e -> Mono.empty())
                                .then()); // Legend Master
                    }
                    // Award quest mastery badge
                    if ((boolean) status.get("allQuestsCompleted") && !badgeAwards.isEmpty()) {
                        badgeAwards.add(badgeService.awardBadgeToUser(userId, BADGE_JAVA_MASTER)
                                .onErrorResume(e -> Mono.empty())
                                .then()); // Java Master
                    }

                    if (badgeAwards.isEmpty()) {
                        return Mono.empty();
                    }

                    return Mono.when(badgeAwards);
                });
    }
}

//    public Mono<UserTaskProgress> startTask(String userId, String taskId) {
//        return taskRepo.findById(taskId)
//                .switchIfEmpty(Mono.error(new IllegalStateException("Task not found")))
//                .flatMap(task -> ensureQuestProgressOnStart(userId, task)
//                        .then(taskProgressRepo.findByUserIdAndTaskId(userId, taskId))
//                        .flatMap(existing -> {
//                            // If already completed or in progress, just return it
//                            if ("COMPLETED".equals(existing.getStatus()) ||
//                                "IN_PROGRESS".equals(existing.getStatus())) {
//                                return Mono.just(existing);
//                            }
//                            // Any other status: set to IN_PROGRESS and update
//                            existing.setStatus("IN_PROGRESS");
//                            existing.setUpdatedAt(LocalDateTime.now());
//                            return taskProgressRepo.save(existing);
//                        })
//                        .switchIfEmpty(Mono.defer(() -> {
//                            // No row for this user/task: create a fresh one
//                            UserTaskProgress progress = new UserTaskProgress(
//                                    userId,
//                                    taskId,
//                                    "IN_PROGRESS",
//                                    0,
//                                    LocalDateTime.now()
//                            );
//                            return taskProgressRepo.save(progress);
//                        }))
//                );
//    }
//
//    private Mono<UserQuestProgress> ensureQuestProgressOnStart(String userId, Task task) {
//        String questId = task.getQuestId();
//        if (questId == null) {
//            return Mono.empty();
//        }
//
//        return questProgressRepo.findByUserIdAndQuestId(userId, questId)
//                .switchIfEmpty(Mono.defer(() -> {
//                    UserQuestProgress qp = new UserQuestProgress();
//                    qp.setUserId(userId);
//                    qp.setQuestId(questId);
//                    qp.setStatus("IN_PROGRESS");
//                    qp.setGainedXp(0);
//                    return questProgressRepo.save(qp);
//                }));
//    }
//
//    public Mono<UserTaskProgress> completeTask(String userId, String taskId) {
//        return taskProgressRepo.findByUserIdAndTaskId(userId, taskId)
//                .switchIfEmpty(Mono.error(new IllegalStateException("Task not started")))
//                .flatMap(progress -> {
//                    if (progress.getId() == null) {
//                        return Mono.error(new IllegalStateException("Task progress has null id; recreate progress data"));
//                    }
//
//                    if ("COMPLETED".equals(progress.getStatus())) {
//                        return Mono.just(progress);
//                    }
//
//                    return taskRepo.findById(taskId)
//                            .switchIfEmpty(Mono.error(new IllegalStateException("Task not found")))
//                            .flatMap(task -> {
//                                int xpReward = task.getXpReward() == null ? 0 : task.getXpReward();
//
//                                progress.setStatus("COMPLETED");
//                                progress.setGainedXp(xpReward);
//                                progress.setUpdatedAt(LocalDateTime.now());
//
//                                return taskProgressRepo.save(progress)
//                                        .flatMap(savedProgress ->
//                                                updateUserXp(userId, xpReward)
//                                                        .then(updateQuestProgressOnComplete(userId, task, xpReward))
//                                                        .then(awardDynamicTaskBadges(userId))
//                                                        .thenReturn(savedProgress)
//                                        );
//                            });
//                });
//    }
//
//    private Mono<UserQuestProgress> updateQuestProgressOnComplete(String userId, Task task, int xpReward) {
//        String questId = task.getQuestId();
//        if (questId == null) {
//            return Mono.empty();
//        }
//
//        return questProgressRepo.findByUserIdAndQuestId(userId, questId)
//                .switchIfEmpty(Mono.defer(() -> {
//                    UserQuestProgress qp = new UserQuestProgress();
//                    qp.setUserId(userId);
//                    qp.setQuestId(questId);
//                    qp.setStatus("IN_PROGRESS");
//                    qp.setGainedXp(xpReward);
//                    return questProgressRepo.save(qp);
//                }))
//                .flatMap(qp -> {
//                    int currentXp = qp.getGainedXp() == null ? 0 : qp.getGainedXp();
//                    qp.setGainedXp(currentXp + xpReward);
//
//                    // Check if all tasks in this quest are completed
//                    return isAllQuestTasksCompleted(userId, questId)
//                            .flatMap(allCompleted -> {
//                                if (allCompleted) {
//                                    qp.setStatus("COMPLETED");
//                                    return questProgressRepo.save(qp)
//                                            .flatMap(saved -> awardQuestCompletionBadges(userId)
//                                                    .thenReturn(saved));
//                                } else {
//                                    return questProgressRepo.save(qp);
//                                }
//                            });
//                });
//    }
//
//    private Mono<Boolean> isAllQuestTasksCompleted(String userId, String questId) {
//        return taskRepo.findByQuestId(questId)
//                .flatMap(task -> taskProgressRepo.findByUserIdAndTaskId(userId, task.getId())
//                        .map(tp -> "COMPLETED".equals(tp.getStatus()))
//                        .defaultIfEmpty(false)
//                )
//                .collectList()
//                .map(results -> results.isEmpty() || results.stream().allMatch(b -> b));
//    }
//
//    private Mono<Void> updateUserXp(String userId, Integer xpGain) {
//        return userRepo.findById(userId)
//                .flatMap(user -> {
//                    user.setTotalXp((user.getTotalXp() != null ? user.getTotalXp() : 0) + xpGain);
//                    return userRepo.save(user);
//                })
//                .then();
//    }
//
//    private Mono<Void> awardDynamicTaskBadges(String userId) {
//        return taskProgressRepo.findByUserId(userId)
//                .filter(tp -> "COMPLETED".equals(tp.getStatus()))
//                .count()
//                .flatMap(count -> {
//                    java.util.List<Mono<Void>> badgeAwards = new java.util.ArrayList<>();
//
//                    // Award badge for first task completion
//                    if (count == 1) {
//                        badgeAwards.add(badgeService.awardBadgeToUser(userId, "badge-1")
//                                .onErrorResume(e -> Mono.empty())
//                                .then());
//                    }
//                    // Award badge for 5 tasks completed
//                    if (count == 5) {
//                        badgeAwards.add(badgeService.awardBadgeToUser(userId, "badge-3")
//                                .onErrorResume(e -> Mono.empty())
//                                .then());
//                    }
//                    // Award badge for 10 tasks completed
//                    if (count == 10) {
//                        badgeAwards.add(badgeService.awardBadgeToUser(userId, "badge-4")
//                                .onErrorResume(e -> Mono.empty())
//                                .then());
//                    }
//
//                    if (badgeAwards.isEmpty()) {
//                        return Mono.empty();
//                    }
//
//                    return Mono.when(badgeAwards);
//                });
//    }
//
//    private Mono<Void> awardQuestCompletionBadges(String userId) {
//        return questProgressRepo.findByUserId(userId)
//                .filter(qp -> "COMPLETED".equals(qp.getStatus()))
//                .count()
//                .flatMap(count -> {
//                    java.util.List<Mono<Void>> badgeAwards = new java.util.ArrayList<>();
//
//                    // Award badge for first quest completion
//                    if (count == 1) {
//                        badgeAwards.add(badgeService.awardBadgeToUser(userId, "badge-5")
//                                .onErrorResume(e -> Mono.empty())
//                                .then());
//                    }
//                    // Award badge for completing 3 quests
//                    if (count == 3) {
//                        badgeAwards.add(badgeService.awardBadgeToUser(userId, "badge-6")
//                                .onErrorResume(e -> Mono.empty())
//                                .then());
//                    }
//                    // Award badge for completing all quests
//                    if (count == 2) { // Assuming 2 quests total in seed data
//                        badgeAwards.add(badgeService.awardBadgeToUser(userId, "badge-7")
//                                .onErrorResume(e -> Mono.empty())
//                                .then());
//                    }
//
//                    if (badgeAwards.isEmpty()) {
//                        return Mono.empty();
//                    }
//
//                    return Mono.when(badgeAwards);
//                });
//    }
//
//    public Mono<UserTaskProgress> getUserTaskProgress(String userId, String taskId) {
//        return taskProgressRepo.findByUserIdAndTaskId(userId, taskId);
//    }
//
//    public Mono<UserQuestProgress> getUserQuestProgress(String userId, String questId) {
//        return questProgressRepo.findByUserIdAndQuestId(userId, questId);
//    }
//
//    public Flux<UserQuestProgress> getAllUserQuestProgress(String userId) {
//        return questProgressRepo.findByUserId(userId);
//    }
//
//    public Flux<UserTaskProgress> getAllUserTaskProgress(String userId) {
//        return taskProgressRepo.findByUserId(userId);
//    }
//
//    public Mono<Map<String, Object>> getUserQuestWithTaskProgress(String userId, String questId) {
//        return questProgressRepo.findByUserIdAndQuestId(userId, questId)
//                .switchIfEmpty(Mono.error(new IllegalStateException("Quest not started")))
//                .flatMap(questProgress -> {
//                    // Get quest details
//                    return Mono.just(questProgress);
//                })
//                .flatMap(questProgress ->
//                        taskRepo.findByQuestId(questId)
//                                .flatMap(task -> taskProgressRepo.findByUserIdAndTaskId(userId, task.getId())
//                                        .map(taskProgress -> Map.of(
//                                                "taskId", task.getId(),
//                                                "taskTitle", task.getTitle(),
//                                                "status", taskProgress.getStatus(),
//                                                "gainedXp", taskProgress.getGainedXp()
//                                        ))
//                                        .switchIfEmpty(Mono.just(Map.of(
//                                                "taskId", task.getId(),
//                                                "taskTitle", task.getTitle(),
//                                                "status", "NOT_STARTED",
//                                                "gainedXp", 0
//                                        )))
//                                )
//                                .collectList()
//                                .map(taskList -> Map.of(
//                                        "questId", questId,
//                                        "questProgress", Map.of(
//                                                "status", questProgress.getStatus(),
//                                                "gainedXp", questProgress.getGainedXp()
//                                        ),
//                                        "tasks", taskList
//                                ))
//                );
//    }
//
//    public Mono<Map<String, Object>> getUserCompletionStatus(String userId) {
//        return Mono.zip(
//                taskProgressRepo.findByUserId(userId)
//                        .filter(tp -> "COMPLETED".equals(tp.getStatus()))
//                        .count(),
//                questProgressRepo.findByUserId(userId)
//                        .filter(qp -> "COMPLETED".equals(qp.getStatus()))
//                        .count(),
//                userRepo.findById(userId),
//                badgeService.getUserBadges(userId).count()
//        ).flatMap(tuple -> {
//            long completedTasks = tuple.getT1();
//            long completedQuests = tuple.getT2();
//            User user = tuple.getT3();
//            long badgesEarned = tuple.getT4();
//
//            return taskRepo.findAll().count()
//                    .flatMap(totalTasks ->
//                            questRepo.findAll().count()
//                                    .map(totalQuests -> {
//                                        boolean isAllTasksCompleted = completedTasks == totalTasks && totalTasks > 0;
//                                        boolean isAllQuestsCompleted = completedQuests == totalQuests && totalQuests > 0;
//
//                                        Map<String, Object> status = new java.util.HashMap<>();
//                                        status.put("userId", userId);
//                                        status.put("totalXp", user.getTotalXp());
//                                        status.put("tasksCompleted", completedTasks);
//                                        status.put("tasksTotal", totalTasks);
//                                        status.put("questsCompleted", completedQuests);
//                                        status.put("questsTotal", totalQuests);
//                                        status.put("badgesEarned", badgesEarned);
//                                        status.put("allTasksCompleted", isAllTasksCompleted);
//                                        status.put("allQuestsCompleted", isAllQuestsCompleted);
//                                        status.put("isFullyCompleted", isAllTasksCompleted && isAllQuestsCompleted);
//
//                                        return status;
//                                    })
//                    );
//        });
//    }
//
//    public Mono<Void> awardMasteryBadges(String userId) {
//        return getUserCompletionStatus(userId)
//                .flatMap(status -> {
//                    java.util.List<Mono<Void>> badgeAwards = new java.util.ArrayList<>();
//
//                    // Award ultimate mastery badge for completing everything
//                    if ((boolean) status.get("isFullyCompleted")) {
//                        badgeAwards.add(badgeService.awardBadgeToUser(userId, "badge-8")
//                                .onErrorResume(e -> Mono.empty())
//                                .then()); // Legend Master
//                    }
//                    // Award quest mastery badge
//                    if ((boolean) status.get("allQuestsCompleted") && !badgeAwards.isEmpty()) {
//                        badgeAwards.add(badgeService.awardBadgeToUser(userId, "badge-9")
//                                .onErrorResume(e -> Mono.empty())
//                                .then()); // Java Master
//                    }
//
//                    if (badgeAwards.isEmpty()) {
//                        return Mono.empty();
//                    }
//
//                    return Mono.when(badgeAwards);
//                });
//    }
//}
