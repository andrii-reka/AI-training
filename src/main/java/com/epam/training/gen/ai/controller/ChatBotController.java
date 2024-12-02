package com.epam.training.gen.ai.controller;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Chat-Bot Controller", description = "Controller for chat-bot interactions")
public class ChatBotController {

    @Autowired
    private Kernel semanticKernel;

    @Operation(summary = "Get chat-bot response", description = "Takes a user input and returns a chat-bot response in JSON format.")
    @GetMapping("/chat")
    public Map<String, String> getChatBotResponse(@RequestParam String input) {
        Map<String, String> response = new HashMap<>();

        String botResponse = generateResponse(input);
        response.put("response", botResponse);

        return response;
    }

    private String generateResponse(String input) {
        // Use Semantic Kernel to generate the response
        try {
            FunctionResult<Object> result = semanticKernel.invokePromptAsync(input).block();
            return String.valueOf(result.getResult());
        } catch (Exception e) {
            return "Error generating response: " + e.getMessage();
        }
    }
}