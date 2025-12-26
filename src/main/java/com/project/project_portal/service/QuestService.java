package com.project.project_portal.service;

import com.project.project_portal.dto.Quest;
import com.project.project_portal.repo.QuestRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * QuestService handles quest management operations including:
 * - Retrieving quests by ID or topic
 * - Creating, updating, and deleting quests
 *
 * A quest is a collection of tasks grouped by learning objective.
 * Quests serve as intermediate organizational units between Topics and Tasks.
 */
@Service
public class QuestService {

    private final QuestRepository repository;

    public QuestService(QuestRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all quests in the system.
     *
     * @return Flux<Quest> of all quests
     */
    public Flux<Quest> getAllQuests() {
        return repository.findAll();
    }

    /**
     * Retrieves a specific quest by ID.
     *
     * @param id The quest ID
     * @return Mono<Quest> or empty if not found
     */
    public Mono<Quest> getQuestById(String id) {
        return repository.findById(id);
    }

    /**
     * Retrieves all quests for a specific topic, ordered by their sequence.
     *
     * @param topicId The topic ID
     * @return Flux<Quest> ordered by orderIndex
     */
    public Flux<Quest> getQuestsByTopicId(String topicId) {
        return repository.findByTopicIdOrderByOrderIndexAsc(topicId);
    }

    /**
     * Creates a new quest.
     *
     * @param quest The quest to create
     * @return Mono<Quest> with persisted data
     */
    public Mono<Quest> createQuest(Quest quest) {
        return repository.save(quest);
    }

    /**
     * Updates an existing quest.
     * Merges updated fields with existing quest data.
     *
     * @param id The quest ID to update
     * @param quest The updated quest data
     * @return Mono<Quest> with merged data or empty if quest not found
     */
    public Mono<Quest> updateQuest(String id, Quest quest) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setName(quest.getName());
                    existing.setDescription(quest.getDescription());
                    existing.setTopicId(quest.getTopicId());
                    existing.setOrderIndex(quest.getOrderIndex());
                    return repository.save(existing);
                });
    }

    /**
     * Deletes a quest and all associated tasks.
     * Cascading delete is handled by database constraints.
     *
     * @param id The quest ID to delete
     * @return Mono<Void>
     */
    public Mono<Void> deleteQuest(String id) {
        return repository.deleteById(id);
    }
}