package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.ChatMessage;
import com.epam.training.gen.ai.service.imp.ChatBotService;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Chat-Bot Controller", description = "Controller for chat-bot interactions")
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @Operation(summary = "Get chat-bot response", description = "Takes a user input and returns a chat-bot response in JSON format.")
    @GetMapping("/chat")
    public Map<String, ChatMessage> getChatBotResponse(@RequestParam String prompt) throws ServiceNotFoundException {
        Map<String, ChatMessage> response = new HashMap<>();

        response.put("response", chatBotService.getResponse(prompt));

        return response;
    }

    @Operation(summary = "Get chat-bot history", description = "Provides User chat history in JSON format.")
    @GetMapping("/history")
    public List<ChatMessage> getChatBotResponse() {
        return chatBotService.getHistory();
    }

    @Operation(summary = "Clean a history and setup new context", description = "Provides new context for a chat")
    @PostMapping("/context")
    public Map<String, String> setContext(String context) {
        Map<String, String> response = new HashMap<>();

        chatBotService.setContext(context);

        response.put("response", "Context set");

        return response;
    }

    @Operation(summary = "Clean a history and setup new AI model", description = "Provides new context for a chat")
    @GetMapping("/set-deployment-name")
    public String setModel(@RequestParam(defaultValue = "Mixtral-8x7B-Instruct-v0.1") String deploymentName) {

        chatBotService.setModel(deploymentName);
        return String.format("Deployment updated to %s", deploymentName);
    }

}