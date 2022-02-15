package com.itechart.kafkaservice.service.impl;

import com.itechart.kafkaservice.dto.EventDto;
import com.itechart.kafkaservice.service.KafkaConsumerService;
import com.itechart.kafkaservice.service.TargetService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl implements KafkaConsumerService {
    private final KafkaAdmin kafkaAdmin;
    private final TargetService targetService;
    private final ConsumerFactory<String, EventDto> consumerFactory;
    private final KafkaTemplate<String, EventDto> template;

    private KafkaMessageListenerContainer<String, EventDto> listener;

    @Override
    public void createTopic(String topic) {
//        Map<String, TopicDescription> description = kafkaAdmin.describeTopics(topic); //throws exception
//        if (!description.isEmpty()) return;

        kafkaAdmin.createOrModifyTopics(
                TopicBuilder
                        .name(topic)
                        .replicas(1)
                        .build()
        );

//        listener = newListenerContainerFor(topic); //unneeded
//        listener.start();
    }

    @Override
    public void reassignPartitionsAmount(String topic, int newPartitionsAmount) {
        Map<String, TopicDescription> description = kafkaAdmin.describeTopics(topic);
        if (description.isEmpty() || description.get(topic).partitions().size() >= newPartitionsAmount) return;

        kafkaAdmin.createOrModifyTopics(
                TopicBuilder
                        .name(topic)
                        .partitions(newPartitionsAmount)
                        .replicas(1)
                        .build()
        );
    }

    @Override
    public void subscribe(final String topic) {
        if (listener != null && listener.isRunning()) return;

        System.out.println("Subscribing to " + topic);
        listener = newListenerContainerFor(topic);
        listener.start();
    }

    @Override
    public void unsubscribe(final String topic) {
        if (listener != null && !listener.isRunning()) return;

        System.out.println("Unsubscribing from " + topic);
        listener.stop();
    }

    public KafkaMessageListenerContainer<String, EventDto> newListenerContainerFor(String topic) {
        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setMessageListener((MessageListener<String, EventDto>) record
                -> targetService.invoke(record.value()));

        return new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws InterruptedException {
        System.out.println("READY");
        String topic = "test-topic8";

        createTopic(topic);
        TopicDescription topicDescription = kafkaAdmin.describeTopics(topic).get(topic);
        System.out.println(topicDescription.name() + " has " + topicDescription.partitions().size() + " prttns");

//        template.send(topic, new EventDto("in-test-location", LocalDateTime.now()));

//        reassignPartitionsAmount(topic, 7);
//        System.out.println(topicDescription.name() + " has " + topicDescription.partitions().size() + " prttns");
//
//        reassignPartitionsAmount(topic, 3);
//        System.out.println(topicDescription.name() + " has " + topicDescription.partitions().size() + " prttns");

//            unsubscribe(topic);
//        template.send(topic, new EventDto("out-test-location", LocalDateTime.now()));

        subscribe(topic);
        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                template.send(topic, new EventDto("out-test-location-" + i, LocalDateTime.now()));
                System.out.println("MSG " + i + "SENT.");
            }
        });
        t.start();
        t.join();
        unsubscribe(topic);
    }
}
