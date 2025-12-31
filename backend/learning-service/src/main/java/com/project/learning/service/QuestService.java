package com.project.learning.service;

import com.project.common.exception.ResourceNotFoundException;
import com.project.learning.domain.Quest;
import com.project.learning.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;

    public Flux<Quest> getQuestsByTopicId(String topicId) {
        log.debug("Fetching quests for topic: {}", topicId);
        return questRepository.findByTopicIdOrderBySequenceOrder(topicId);
    }

    public Mono<Quest> getQuestById(String id) {
        return questRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Quest", id)));
    }

    public Mono<Quest> createQuest(Quest quest) {
        return questRepository.save(quest);
    }
}
