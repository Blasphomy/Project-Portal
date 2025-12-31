package com.project.learning.service;

import com.project.learning.domain.UserProgress;
import com.project.learning.event.TaskCompletedEvent;
import com.project.learning.repository.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final UserProgressRepository progressRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Flux<UserProgress> getUserProgress(String userId) {
        return progressRepository.findByUserId(userId);
    }

    public Mono<UserProgress> getProgressByUserAndTask(String userId, String taskId) {
        return progressRepository.findByUserIdAndTaskId(userId, taskId);
    }

    public Mono<UserProgress> completeTask(String userId, String taskId, Integer xpEarned, String codeSubmission) {
        return progressRepository.findByUserIdAndTaskId(userId, taskId)
                .defaultIfEmpty(UserProgress.builder()
                        .userId(userId)
                        .taskId(taskId)
                        .status("IN_PROGRESS")
                        .attempts(0)
                        .build())
                .flatMap(progress -> {
                    progress.setStatus("COMPLETED");
                    progress.setXpEarned(xpEarned);
                    progress.setCodeSubmission(codeSubmission);
                    progress.setCompletedAt(LocalDateTime.now());
                    progress.setAttempts(progress.getAttempts() + 1);

                    return progressRepository.save(progress)
                            .doOnSuccess(saved -> {
                                // Publish event for XP and badge processing
                                TaskCompletedEvent event = TaskCompletedEvent.builder()
                                        .userId(userId)
                                        .taskId(taskId)
                                        .xpEarned(xpEarned)
                                        .completedAt(LocalDateTime.now())
                                        .build();
                                eventPublisher.publishEvent(event);
                                log.info("Task completed event published for user: {}, task: {}", userId, taskId);
                            });
                });
    }
}
