package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.service.imp.ChatBotService;
import com.epam.training.gen.ai.service.imp.EmbeddingsService;
import com.epam.training.gen.ai.util.RAGSourceFileUtil;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import io.qdrant.client.grpc.Points;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@RestController("/rag")
@Tag(name = "RAG Controller", description = "Controller for RAG")
public class RAGController {

    private static final String RAG_COLLECTION = "rag-collection";
    private static final String DEFAULT_RAG_FILE_PATH = "/rag-source.txt";

    @Autowired
    private EmbeddingsService embeddingsService;
    @Autowired
    private ChatBotService chatBotService;

    @Operation(summary = "Specify RAG source file in resource folder", description = "Specify RAG source")
    @PostMapping("/upload-source-file")
    public Map<String, String> uploadRAGSourceFile(@RequestParam(defaultValue = RAG_COLLECTION) String collectionName,
                                                   @RequestParam(defaultValue = DEFAULT_RAG_FILE_PATH) String fileRelativePath) throws ExecutionException, InterruptedException, IOException {
        Map<String, String> response = new HashMap<>();
        File file = new File(getClass().getClassLoader().getResource(fileRelativePath).getFile());
//        List<String> segments = RAGSourceFileUtil.splitFileIntoSentences(file);
        List<String> segments = RAGSourceFileUtil.splitFileByCharsWithOverlap(file, 500, 50);
        segments.forEach(segment -> {
            try {
                embeddingsService.processAndSaveText(collectionName, segment);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        response.put("response", "Saved %d segments".formatted(segments.size()));

        return response;
    }

    @Operation(summary = "Specify RAG source", description = "Specify RAG source")
    @PostMapping("/rag-prompt")
    public Map<String, String> askQuestion(@RequestParam(defaultValue = RAG_COLLECTION) String collectionName, String question) throws ExecutionException, InterruptedException, ServiceNotFoundException {
        Map<String, String> response = new HashMap<>();

        List<Points.ScoredPoint> search = embeddingsService.search(collectionName, question);

        String context = search.stream()
                .map(scoredPoint -> String.valueOf(scoredPoint.getPayloadMap().get("info")))
                .collect(Collectors.joining(" ", "<<", ">>"));
        chatBotService.setContext(context);
        response.put("response", chatBotService.getResponse(question).getMessage());

        return response;
    }


}
