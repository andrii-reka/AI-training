package com.epam.training.gen.ai.service.imp;

import com.epam.training.gen.ai.config.AIProperties;
import com.epam.training.gen.ai.config.SemanticKernelProvider;
import com.epam.training.gen.ai.model.ChatMessage;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatBotService {

    @Autowired
    private AIProperties aiProperties;
    @Autowired
    private SemanticKernelProvider semanticKernelProvider;

    private ChatHistory chatHistory = new ChatHistory();

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBotService.class);

    /**
     * Retrieves a response from the chatbot based on the given prompt.
     *
     * @param prompt User's input prompt.
     * @return The chatbot's response message.
     * @throws ServiceNotFoundException If there's an issue while fetching response.
     */
    public ChatMessage getResponse(String prompt) throws ServiceNotFoundException {
        chatHistory.addUserMessage(prompt);

        List<ChatMessageContent<?>> results = semanticKernelProvider.chatCompletionService().getChatMessageContentsAsync(chatHistory, semanticKernelProvider.semanticKernel(), semanticKernelProvider.invocationContext()).block();


        for (ChatMessageContent<?> result : results) {
            if (result.getAuthorRole() == AuthorRole.ASSISTANT && result.getContent() != null) {
                LOGGER.info("Assistant > " + result);
            }
            chatHistory.addMessage(result);
        }

        ChatMessageContent<?> lastMessage = chatHistory.getLastMessage().orElseThrow(() ->
                new ServiceNotFoundException("No messages in chat history"));
        return new ChatMessage(lastMessage.getAuthorRole().toString(), lastMessage.getContent());
    }

    /**
     * Sets the context for the chat by starting with a system message.
     *
     * @param context Initial context message.
     */
    public void setContext(String context) {
        cleanChat();
        LOGGER.info("Set context: {}", context);
        chatHistory.addSystemMessage(context);
    }

    /**
     * Sets the mode for the chat.
     *
     * @param deploymentName AI model.
     */
    public void setModel(String deploymentName) {
        aiProperties.setDeployment(deploymentName);
        semanticKernelProvider.refreshBeans();
        cleanChat();

        LOGGER.info("Set model: {}", deploymentName);


    }

    /**
     * Clears the chat history.
     */
    public void cleanChat() {
        LOGGER.info("Cleaning chat history");
        chatHistory = new ChatHistory();
    }

    /**
     * Retrieves the entire chat history.
     *
     * @return List of all chat messages.
     */
    public List<ChatMessage> getHistory() {
        return chatHistory.getMessages().stream()
                .map(msgContent -> new ChatMessage(
                        msgContent.getAuthorRole().toString(),
                        msgContent.getContent()))
                .collect(Collectors.toList());
    }
}
