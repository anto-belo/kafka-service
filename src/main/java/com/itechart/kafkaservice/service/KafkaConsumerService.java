package com.itechart.kafkaservice.service;

import org.springframework.stereotype.Service;

@Service
public interface KafkaConsumerService {
    void createTopic(String topicName);

    void reassignPartitionsAmount(String topicName, int partitionsAmount);

    void subscribe(final String topic);

    void unsubscribe(final String topic);
}
