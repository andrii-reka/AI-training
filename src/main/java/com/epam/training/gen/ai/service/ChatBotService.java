package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatBotService {

    @Autowired
    private ChatHistory chatHistory;
    @Autowired
    private Kernel kernel;
    @Autowired
    private InvocationContext invocationContext;

    public String getResponse(String prompt) throws ServiceNotFoundException {

        ChatCompletionService chatCompletionService = kernel.getService(
                ChatCompletionService.class);
        //chatCompletionService.getChatMessageContentsAsync(prompt, kernel, invocationContext);
        List<ChatMessageContent<?>> results = chatCompletionService.getChatMessageContentsAsync(
                prompt, kernel, null).block();
        chatHistory.addUserMessage(prompt);
        for (ChatMessageContent<?> result : results) {
            // Print the results
            if (result.getAuthorRole() == AuthorRole.ASSISTANT && result.getContent() != null) {
                System.out.println("Assistant > " + result);
            }
            // Add the message from the agent to the chat history
            chatHistory.addMessage(result);
        }
        return chatHistory.getLastMessage().get().getContent();
    }

    public String getHistory(){
        StringBuilder stringBuilder = new StringBuilder();
        chatHistory.forEach(chatMessageContent -> {
            if (chatMessageContent.getAuthorRole() == AuthorRole.ASSISTANT) {
                stringBuilder.append("Bot: ").append(chatMessageContent.getContent());
            } else if (chatMessageContent.getAuthorRole() == AuthorRole.USER) {
                stringBuilder.append("User: ").append(chatMessageContent.getContent());
            }
            stringBuilder.append(System.getProperty("line.separator"));
        });
        return stringBuilder.toString();
    }
}
