package com.project.ai.service;

import io.pinecone.clients.Pinecone;
import io.pinecone.proto.CreateIndexRequest;
import io.pinecone.proto.DescribeIndexStatsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class PineconeService {

    private final String apiKey;
    private final String environment;
    private final String indexName;
    private Pinecone pinecone;

    public PineconeService(
            @Value("${pinecone.api.key}") String apiKey,
            @Value("${pinecone.environment}") String environment,
            @Value("${pinecone.index.name}") String indexName) {
        this.apiKey = apiKey;
        this.environment = environment;
        this.indexName = indexName;
    }

    @PostConstruct
    public void init() {
        this.pinecone = new Pinecone(apiKey, environment);
        createIndexIfNotExists();
    }

    private void createIndexIfNotExists() {
        if (!indexExists()) {
            pinecone.createIndex(CreateIndexRequest.newBuilder()
                    .setName(indexName)
                    .setDimension(1024) // Standard for Cohere embeddings
                    .build());
        }
    }

    private boolean indexExists() {
        try {
            pinecone.describeIndexStats(DescribeIndexStatsRequest.newBuilder()
                    .setIndexName(indexName)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Pinecone getPinecone() {
        return pinecone;
    }

    public String getIndexName() {
        return indexName;
    }
}
