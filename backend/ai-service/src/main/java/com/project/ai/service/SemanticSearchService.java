package com.project.ai.service;

import com.cohere.api.Cohere;
import com.cohere.api.Embeddings;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.pinecone.clients.Pinecone;
import io.pinecone.proto.QueryRequest;
import io.pinecone.proto.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SemanticSearchService {

    private final PineconeService pineconeService;
    private final Cohere cohere;

    @Autowired
    public SemanticSearchService(PineconeService pineconeService, @Value("${cohere.api.key}") String cohereApiKey) {
        this.pineconeService = pineconeService;
        this.cohere = new Cohere(cohereApiKey);
    }

    public List<String> semanticSearch(String query, int topK) {
        Embeddings embeddings = cohere.embed(List.of(query), "embed-english-v3.0").join();
        List<Float> queryVector = embeddings.getEmbeddings().get(0);

        Pinecone pinecone = pineconeService.getPinecone();
        String indexName = pineconeService.getIndexName();

        QueryRequest queryRequest = QueryRequest.newBuilder()
                .setIndexName(indexName)
                .addAllVector(queryVector)
                .setTopK(topK)
                .setIncludeMetadata(true)
                .build();

        QueryResponse queryResponse = pinecone.getBlockingStub().query(queryRequest);

        return queryResponse.getMatchesList().stream()
                .map(match -> match.getMetadata().getFieldsMap().get("text").getStringValue())
                .collect(Collectors.toList());
    }
}
