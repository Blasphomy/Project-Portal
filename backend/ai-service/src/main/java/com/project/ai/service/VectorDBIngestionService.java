package com.project.ai.service;

import com.cohere.api.Cohere;
import com.project.ai.client.LearningServiceClient;
import com.project.ai.dto.LearningMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorDBIngestionService {

    private final PineconeService pineconeService;
    private final Cohere cohere;
    private final LearningServiceClient learningServiceClient;

    @Autowired
    public VectorDBIngestionService(PineconeService pineconeService, 
                                    @Value("${cohere.api.key}") String cohereApiKey, 
                                    LearningServiceClient learningServiceClient) {
        this.pineconeService = pineconeService;
        this.cohere = new Cohere(cohereApiKey);
        this.learningServiceClient = learningServiceClient;
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void ingestLearningMaterials() {
        List<LearningMaterial> materials = learningServiceClient.getAllLearningMaterials();
        
        // The rest of the ingestion logic will be implemented in a future step.
    }
}
