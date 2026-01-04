package com.project.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class PineconeService {

    private final String apiKey;
    private final String environment;
    private final String indexName;

    public PineconeService(
            @Value("${pinecone.api.key:}") String apiKey,
            @Value("${pinecone.environment:}") String environment,
            @Value("${pinecone.index.name:}") String indexName) {
        this.apiKey = apiKey;
        this.environment = environment;
        this.indexName = indexName;
    }

    @PostConstruct
    public void init() {
        System.out.println("PineconeService initialized (Mock Mode)");
    }

    public String getIndexName() {
        return indexName;
    }

    // public Pinecone getPinecone() { return null; }
}
