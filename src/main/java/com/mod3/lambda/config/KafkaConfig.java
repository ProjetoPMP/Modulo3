package com.mod3.lambda.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${app.kafka.topic-name}")
    private String topicName;

    @Bean
    public NewTopic topic() {
        return new NewTopic(topicName, 1, (short) 1);
    }
}
