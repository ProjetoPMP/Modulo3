package com.mod3.lambda.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaLambdaListener {

    @KafkaListener(
            topics = "${app.kafka.topic-name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message) {
        System.out.println("A mensagem chegou: " + message);
    }
}
