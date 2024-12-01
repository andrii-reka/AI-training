package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.service.ChatBotService;
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
    private ChatBotService chatBotService;

    @Operation(summary = "Get chat-bot response", description = "Takes a user input and returns a chat-bot response in JSON format.")
    @GetMapping("/chat")
    public Map<String, String> getChatBotResponse(@RequestParam String input) {
        Map<String, String> response = new HashMap<>();

        String botResponse = generateResponse(input);
        response.put("response", botResponse);

        return response;
    }

    @Operation(summary = "Get chat-bot history", description = "Provides User chat history in JSON format.")
    @GetMapping("/history")
    public Map<String, String> getChatBotResponse() {
        Map<String, String> response = new HashMap<>();

        response.put("response", chatBotService.getHistory());

        return response;
    }

    private String generateResponse(String input) {
        // Use Semantic Kernel to generate the response
        try {
            return chatBotService.getResponse(input);
        } catch (Exception e) {
            return "Error generating response: " + e.getMessage();
        }
    }
}