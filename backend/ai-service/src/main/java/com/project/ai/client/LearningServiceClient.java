package com.project.ai.client;

import com.project.ai.dto.LearningMaterial;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "learning-service", url = "${learning.service.url}")
public interface LearningServiceClient {

    @GetMapping("/api/learning-materials")
    List<LearningMaterial> getAllLearningMaterials();
}
