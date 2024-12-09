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
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SemanticKernelProvider {

    @Autowired
    private AIProperties aiProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticKernelProvider.class);

    private OpenAIAsyncClient openAIAsyncClientSupplier;
    private InvocationContext invocationContextSupplier;
    private ChatCompletionService chatCompletionServiceSupplier;
    private Kernel semanticKernelSupplier;

    @PostConstruct
    public void initialize() {

//        this.openAIAsyncClientSupplier = () -> new OpenAIClientBuilder()
//                .endpoint(aiProperties.getEndpoint())
//                .credential(new AzureKeyCredential(aiProperties.getEndpoint()))
//                .buildAsyncClient();
//
//        this.invocationContextSupplier = () -> InvocationContext.builder()
//                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
//                .withReturnMode(InvocationReturnMode.FULL_HISTORY)
//                .withPromptExecutionSettings(
//                        PromptExecutionSettings.builder()
//                                .withTemperature(aiProperties.getTemperature())
//                                .build()
//                )
//                .build();
//
//        this.chatCompletionServiceSupplier = () -> OpenAIChatCompletion.builder()
//                .withModelId(aiProperties.getDeployment())
//                .withOpenAIAsyncClient(openAIAsyncClientSupplier.get())
//                .build();
//
//        this.semanticKernelSupplier = () -> Kernel.builder()
//                .withAIService(ChatCompletionService.class, chatCompletionServiceSupplier.get())
//                .build();

        /// ///////////////////////////////////
        openAIAsyncClientSupplier =  new OpenAIClientBuilder()
                    .endpoint(aiProperties.getEndpoint())
                    .credential(new AzureKeyCredential(aiProperties.getKey()))
                    .buildAsyncClient();


        invocationContextSupplier = InvocationContext.builder()
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .withReturnMode(InvocationReturnMode.FULL_HISTORY)
                .withPromptExecutionSettings(
                        PromptExecutionSettings.builder()
                                .withTemperature(aiProperties.getTemperature())
                                .build()
                )
                .build();

        chatCompletionServiceSupplier = OpenAIChatCompletion.builder()
                .withModelId(aiProperties.getDeployment())
                .withOpenAIAsyncClient(openAIAsyncClientSupplier)
                .build();

        semanticKernelSupplier = Kernel.builder()
                    .withAIService(ChatCompletionService.class, chatCompletionServiceSupplier)
                    .build();

        Map<String, PromptExecutionSettings> promptExecutionSettingsMap = Map.of(aiProperties.getDeployment(), PromptExecutionSettings.builder()
                .withTemperature(1.0)
                .build());
    }

    public OpenAIAsyncClient openAIAsyncClient() {
        return openAIAsyncClientSupplier;
    }

    public InvocationContext invocationContext() {
        return invocationContextSupplier;
    }

    public ChatCompletionService chatCompletionService() {
        return chatCompletionServiceSupplier;
    }

    public Kernel semanticKernel() {
        return semanticKernelSupplier;
    }

    public void refreshBeans() {
        initialize();
    }
}