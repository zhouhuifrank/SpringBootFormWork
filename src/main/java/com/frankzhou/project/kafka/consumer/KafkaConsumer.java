package com.frankzhou.project.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 测试Kafka消费者
 * @date 2023-08-19
 */
//@Slf4j
//@Component
//public class KafkaConsumer {
//
//    @KafkaListener(topics = KafkaProducer.TOPIC_NAME, groupId = KafkaProducer.TEST_GROUP_NAME)
//    public void topicTest(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//        Optional<?> message = Optional.ofNullable(record.value());
//        if (message.isPresent()) {
//            Object msg = message.get();
//            log.info("kafka消费了{}的消息，msg:{}",KafkaProducer.TOPIC_NAME,msg);
//            ack.acknowledge();
//        }
//    }
//
//}
