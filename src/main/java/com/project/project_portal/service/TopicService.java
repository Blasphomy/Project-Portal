package com.project.project_portal.service;

import com.project.project_portal.dto.Quest;
import com.project.project_portal.dto.Task;
import com.project.project_portal.dto.Topic;
import com.project.project_portal.dto.TopicTreeView;
import com.project.project_portal.repo.QuestRepository;
import com.project.project_portal.repo.TaskRepository;
import com.project.project_portal.repo.TopicRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * TopicService handles topic management and content organization including:
 * - Retrieving topics and topic hierarchies
 * - Building topic trees with nested quests and tasks
 * - Creating, updating, and deleting topics
 *
 * This service coordinates with QuestService and TaskService to provide
 * a complete hierarchical view of the learning content structure.
 */
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final QuestRepository questRepository;
    private final TaskRepository taskRepository;

    public TopicService(TopicRepository topicRepository,
                        QuestRepository questRepository,
                        TaskRepository taskRepository) {
        this.topicRepository = topicRepository;
        this.questRepository = questRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieves all topics with pagination support.
     *
     * @param page The page number (0-indexed)
     * @param size The number of items per page
     * @return Flux<Topic> of requested page
     */
    public Flux<Topic> getAllTopics(int page, int size) {
        // Note: Pageable parameter is calculated but R2DBC reactive streams handle pagination differently
        // Using skip() and take() for reactive pagination
        return topicRepository.findAll()
                .skip((long) page * size)
                .take(size);
    }

    /**
     * Retrieves a specific topic by ID.
     *
     * @param id The topic ID
     * @return Mono<Topic> or empty if not found
     */
    public Mono<Topic> getTopicById(String id) {
        return topicRepository.findById(id);
    }

    /**
     * Builds a complete hierarchical view of a topic including all its quests and tasks.
     * Provides a tree structure suitable for displaying the learning path.
     *
     * @param topicId The topic ID
     * @return Mono<TopicTreeView> with full hierarchy or empty if topic not found
     */
    public Mono<TopicTreeView> getTopicTree(String topicId) {
        return topicRepository.findById(topicId)
                .flatMap(topic ->
                        questRepository.findByTopicIdOrderByOrderIndexAsc(topicId)
                                .flatMap(quest ->
                                        taskRepository.findByQuestIdOrderByOrderIndexAsc(quest.getId())
                                                .map(task -> new TopicTreeView.TaskView(
                                                        task.getId(),
                                                        task.getTitle(),
                                                        task.getDescription(),
                                                        task.getOrderIndex(),
                                                        task.getXpReward()
                                                ))
                                                .collectList()
                                                .map(taskViews -> new TopicTreeView.QuestView(
                                                        quest.getId(),
                                                        quest.getName(),
                                                        quest.getDescription(),
                                                        quest.getOrderIndex(),
                                                        taskViews
                                                ))
                                )
                                .sort(Comparator.comparingInt(TopicTreeView.QuestView::getOrderIndex))
                                .collectList()
                                .map(questViews -> new TopicTreeView(
                                        topic.getId(),
                                        topic.getName(),
                                        topic.getDescription(),
                                        questViews
                                ))
                );
    }

    /**
     * Creates a new topic.
     *
     * @param topic The topic to create
     * @return Mono<Topic> with persisted data
     */
    public Mono<Topic> createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    /**
     * Updates an existing topic.
     * Merges updated fields with existing topic data.
     *
     * @param id The topic ID to update
     * @param topic The updated topic data
     * @return Mono<Topic> with merged data or empty if topic not found
     */
    public Mono<Topic> updateTopic(String id, Topic topic) {
        return topicRepository.findById(id)
                .flatMap(existing -> {
                    existing.setName(topic.getName());
                    existing.setDescription(topic.getDescription());
                    return topicRepository.save(existing);
                });
    }

    /**
     * Deletes a topic and all associated quests and tasks.
     * Cascading delete is handled by database constraints.
     *
     * @param id The topic ID to delete
     * @return Mono<Void>
     */
    public Mono<Void> deleteTopic(String id) {
        return topicRepository.deleteById(id);
    }
}