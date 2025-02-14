package com.recruitment.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "myTopic", groupId = "recruitment-consumers")
    public void listen(String message) {
        System.out.println("Odebrano wiadomość z Kafki: " + message);
    }
}