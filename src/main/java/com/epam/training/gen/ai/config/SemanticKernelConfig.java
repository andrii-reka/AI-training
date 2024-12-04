package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SemanticKernelConfig {

    @Value("${client-openai-endpoint}")
    private String endpoint;
    @Value("${client-openai-key}")
    private String key;
    @Value("${client-openai-deployment-name:gpt-35-turbo}")
    private String deployment;
    @Value("${prompt-execution-settings-temperature:0.6}")
    private Double temperature;

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticKernelConfig.class);

    /**
     * Creates a {@link OpenAIAsyncClient} with the endpoint and key specified in the
     *
     * @return the {@link OpenAIAsyncClient}
     */
    @Bean
    @ConditionalOnClass(OpenAIAsyncClient.class)
    @ConditionalOnMissingBean(OpenAIAsyncClient.class)
    public OpenAIAsyncClient openAIAsyncClient() {
        return new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(key))
                .buildAsyncClient();
    }


    @Bean
    public InvocationContext invocationContext(@Value("${prompt-execution-settings.temperature:0.6}") Double temperature) {
        return InvocationContext.builder()
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .withReturnMode(InvocationReturnMode.FULL_HISTORY)
                .withPromptExecutionSettings(
                        PromptExecutionSettings.builder()
                                .withTemperature(temperature)
                                .build()
                )
                .build();
    }

    @Bean
    public ChatHistory chatHistory() {
        return new ChatHistory();
    }

    @Bean
    public ChatCompletionService chatCompletionService(OpenAIAsyncClient client) {
        return OpenAIChatCompletion.builder()
                .withModelId(deployment)
                .withOpenAIAsyncClient(client)
                .build();
    }

    /**
     * Creates a {@link Kernel} with a default
     * {@link com.microsoft.semantickernel.services.AIService} that uses the
     *
     * @return the {@link Kernel}
     */
    @Bean
    @ConditionalOnMissingBean(Kernel.class)
    public Kernel semanticKernel(ChatCompletionService chatCompletionService) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
    }

    @Bean
    public Map<String, PromptExecutionSettings> promptExecutionsSettingsMap(
            @Value("${client-openai-deployment-name}") String deploymentName) {
        return Map.of(deploymentName, PromptExecutionSettings.builder()
                .withTemperature(1.0)
                .build());
    }
}