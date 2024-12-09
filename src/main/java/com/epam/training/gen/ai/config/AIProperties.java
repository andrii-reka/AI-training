package com.epam.training.gen.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class AIProperties {

    @Value("${client-openai-endpoint}")
    private String endpoint;
    @Value("${client-openai-key}")
    private String key;
    @Value("${client-openai-deployment-name:gpt-35-turbo}")
    private String deployment;
    @Value("${prompt-execution-settings-temperature:0.6}")
    private Double temperature;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeployment() {
        return deployment;
    }

    public void setDeployment(String deployment) {
        this.deployment = deployment;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
