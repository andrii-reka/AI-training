package com.epam.training.gen.ai.controller;

import com.azure.ai.openai.models.EmbeddingItem;
import com.epam.training.gen.ai.service.imp.EmbeddingsService;
import com.epam.training.gen.ai.service.imp.VectorDBService;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@Tag(name = "Embedding Controller", description = "Controller for Embedding and Vector DB interactions")
public class EmbeddingController {

    @Autowired
    private VectorDBService vectorService;

    @Autowired
    private EmbeddingsService embeddingsService;

    @Operation(summary = "Create Collection", description = "Create Collection")
    @GetMapping("/create-collection")
    public Map<String, String> createCollection(@RequestParam(defaultValue = "test-collection") String collectionName) throws ExecutionException, InterruptedException {
        Map<String, String> response = new HashMap<>();

        response.put("response", vectorService.createCollection(collectionName).toString());

        return response;
    }

    @Operation(summary = "Delete collection", description = "Delete collection")
    @DeleteMapping("/delete-collection")
    public Map<String, String > deleteCollection(@RequestParam(defaultValue = "test-collection") String collectionName) throws ServiceNotFoundException, ExecutionException, InterruptedException {
        Map<String, String> response = new HashMap<>();

        response.put("response", vectorService.deleteCollection(collectionName).toString());

        return response;
    }

    @Operation(summary = "Process text", description = "Process text")
    @GetMapping("/process-text")
    public Map<String, String > processAndSaveText(@RequestParam(defaultValue = "test-collection") String collectionName, @RequestParam String text) throws ExecutionException, InterruptedException {
        Map<String, String> response = new HashMap<>();


        response.put("response", embeddingsService.processAndSaveText(collectionName, text).toString());

        return response;
    }

    @Operation(summary = "Search", description = "search")
    @GetMapping("/search")
    public Map<String, String> search(@RequestParam(defaultValue = "test-collection") String collectionName, @RequestParam String text) throws ExecutionException, InterruptedException {
        Map<String, String> response = new HashMap<>();


        response.put("response", embeddingsService.search(collectionName, text).toString());

        return response;
    }

    @Operation(summary = "Generate embedding", description = "Generate embeddings")
    @GetMapping("/get-embeddings")
    public Map<String, List<EmbeddingItem>> getEmbeddings(@RequestParam String text) throws ExecutionException, InterruptedException {
        Map<String, List<EmbeddingItem>> response = new HashMap<>();


        response.put("response", embeddingsService.getEmbeddings(text));

        return response;
    }

}
