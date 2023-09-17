package com.frankzhou.project.kafka.producer;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.Top;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 测试Kafka生产者
 * @date 2023-08-19
 */
//@Slf4j
//@Component
//public class KafkaProducer {
//
//    @Resource
//    private KafkaTemplate<String, Object> kafkaTemplate;
//
//    public static final String TOPIC_NAME = "hello_kafka";
//
//    public static final String TEST_GROUP_NAME = "test_kafak_group";
//
//    public void sendMessage(Object obj) {
//        String jsonMessage = JSONUtil.toJsonStr(obj);
//        log.info("准备发送消息:{}",jsonMessage);
//
//        // 发送消息 拿到future对象，添加回调逻辑
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC_NAME, jsonMessage);
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                // 失败时的处理逻辑
//                log.info("生产者-{}发送消息失败, error:{}",TOPIC_NAME,ex.getMessage());
//                // 重试
//                sendMessage(obj);
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, Object> result) {
//                // 成功时的处理逻辑
//                log.info("生产者-{}发送消息成功，消息内容为:{}",TOPIC_NAME,JSONUtil.toJsonStr(result));
//            }
//        });
//    }
//}
