package com.epam.training.gen.ai.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {


    // The Java client uses Qdrant's gRPC interface
    @Bean
    public QdrantClient initQdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient.newBuilder("localhost", 6334, false).build());
    }


}
