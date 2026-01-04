package com.project.ai.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SemanticSearchService {

    private final PineconeService pineconeService;

    @Autowired
    public SemanticSearchService(PineconeService pineconeService, @Value("${cohere.api.key:}") String cohereApiKey) {
        this.pineconeService = pineconeService;
    }

    public List<String> semanticSearch(String query, int topK) {
        // Disabled due to SDK version mismatch
        return Collections.emptyList();
    }
}
