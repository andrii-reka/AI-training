package com.epam.training.gen.ai.controller;

import com.azure.ai.openai.models.EmbeddingItem;
import com.epam.training.gen.ai.service.imp.SimpleVectorActions;
import com.epam.training.gen.ai.service.imp.VectorDBService;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import io.qdrant.client.grpc.Collections.CollectionOperationResponse;
import io.qdrant.client.grpc.Points;
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
public class VectorController {

    @Autowired
    private VectorDBService vectorService;

    @Autowired
    private SimpleVectorActions simpleVectorActions;

    @Operation(summary = "Perform simple test (ignore)", description = "")
    @GetMapping("/perform-test")
    public Map<String, String> performTest() throws ServiceNotFoundException, ExecutionException, InterruptedException {
        Map<String, String> response = new HashMap<>();
        vectorService.addVectors();
        vectorService.addFilter();
        vectorService.runQuery();

        response.put("response", "check logs");

        return response;
    }

    @Operation(summary = "Create Collection", description = "")
    @PostMapping("/create-collection")
    public Map<String, CollectionOperationResponse > createCollection(@RequestParam(defaultValue = "test-collection") String collectionName) throws ExecutionException, InterruptedException {
        Map<String, CollectionOperationResponse> response = new HashMap<>();

        response.put("response", vectorService.createCollection(collectionName));

        return response;
    }

    @Operation(summary = "Delete collection", description = "")
    @DeleteMapping("/delete-collection")
    public Map<String, CollectionOperationResponse > deleteCollection(@RequestParam(defaultValue = "test-collection") String collectionName) throws ServiceNotFoundException, ExecutionException, InterruptedException {
        Map<String, CollectionOperationResponse> response = new HashMap<>();

        response.put("response", vectorService.deleteCollection(collectionName));

        return response;
    }


    @Operation(summary = "Process text", description = "")
    @GetMapping("/process-text")
    public Map<String, Points.UpdateResult > processAndSaveText(@RequestParam(defaultValue = "test-collection") String collectionName, @RequestParam String text) throws ExecutionException, InterruptedException {
        Map<String, Points.UpdateResult> response = new HashMap<>();


        response.put("response", simpleVectorActions.processAndSaveText(collectionName, text));

        return response;
    }

    @Operation(summary = "Search", description = "")
    @GetMapping("/search")
    public Map<String, List<Points.ScoredPoint>> search(@RequestParam(defaultValue = "test-collection") String collectionName, @RequestParam String text) throws ExecutionException, InterruptedException {
        Map<String, List<Points.ScoredPoint>> response = new HashMap<>();


        response.put("response", simpleVectorActions.search(collectionName, text));

        return response;
    }

    @Operation(summary = "Generate embedding", description = "")
    @GetMapping("/get-embeddings")
    public Map<String, List<EmbeddingItem>> getEmbeddings(@RequestParam String text) throws ExecutionException, InterruptedException {
        Map<String, List<EmbeddingItem>> response = new HashMap<>();


        response.put("response", simpleVectorActions.getEmbeddings(text));

        return response;
    }

}
