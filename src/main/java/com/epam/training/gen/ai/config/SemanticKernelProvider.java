package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.plugin.CurrencyConverterPlugin;
import com.epam.training.gen.ai.service.CurrencyService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SemanticKernelProvider {

    @Autowired
    private AIProperties aiProperties;
    @Autowired
    private CurrencyService currencyService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticKernelProvider.class);

    private OpenAIAsyncClient openAIAsyncClientSupplier;
    private InvocationContext invocationContextSupplier;
    private ChatCompletionService chatCompletionServiceSupplier;
    private Kernel semanticKernelSupplier;

    @PostConstruct
    public void initialize() {
        openAIAsyncClientSupplier =  new OpenAIClientBuilder()
                    .endpoint(aiProperties.getEndpoint())
                    .credential(new AzureKeyCredential(aiProperties.getKey()))
                    .buildAsyncClient();

        invocationContextSupplier = InvocationContext.builder()
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
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

        KernelPlugin currencyConverterPlugin = KernelPluginFactory.createFromObject(
                new CurrencyConverterPlugin(currencyService),
                "CurrencyConverterPlugin"
        );

        semanticKernelSupplier = Kernel.builder()
                    .withAIService(ChatCompletionService.class, chatCompletionServiceSupplier)
                .withPlugin(currencyConverterPlugin)
                .build();
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