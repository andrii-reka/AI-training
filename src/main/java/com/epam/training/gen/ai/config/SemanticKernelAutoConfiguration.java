package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SemanticKernelAutoConfiguration {

    @Value("${client-openai-endpoint}")
    private String endpoint;
    @Value("${client-openai-key}")
    private String key;
    @Value("${client-openai-deployment-name}")
    private String deploymentName;

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticKernelAutoConfiguration.class);

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

    /**
     * Creates a {@link Kernel} with a default
     * {@link com.microsoft.semantickernel.services.AIService} that uses the
     * @param client
     * @return the {@link Kernel}
     */
    @Bean
    @ConditionalOnMissingBean(Kernel.class)
    public Kernel semanticKernel(OpenAIAsyncClient client) {
        ChatCompletionService chatCompletionService = OpenAIChatCompletion.builder()
                .withModelId(setModelID(deploymentName))
                .withOpenAIAsyncClient(client)
                .build();

        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
    }

    private static String setModelID(String deploymentName) {
        String modelId;
        if (deploymentName == null || deploymentName.isEmpty()) {
            modelId = "gpt-35-turbo";
            LOGGER.warn("No deployment name specified, using default model id: " + modelId);
        } else {
            modelId = deploymentName;
            LOGGER.info("Using model id: " + modelId);
        }
        return modelId;
    }
}