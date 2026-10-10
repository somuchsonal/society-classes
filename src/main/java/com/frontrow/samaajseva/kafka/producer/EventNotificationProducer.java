package com.frontrow.samaajseva.kafka.producer;

import com.frontrow.samaajseva.pojo.request.EventNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Service
@Slf4j
public class EventNotificationProducer {

    @Value("${kafka.eventNotification.topic}")
    private String eventNotificationTopic;

    private final KafkaTemplate<String, EventNotificationRequest> eventNotificationKafkaTemplate;

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Autowired
    public EventNotificationProducer(@Qualifier("eventNotificationKafkaTemplate") KafkaTemplate<String, EventNotificationRequest> eventNotificationKafkaTemplate,
                                     @Qualifier("scheduledExecutor") ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        this.eventNotificationKafkaTemplate = eventNotificationKafkaTemplate;
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
    }

    public void send(EventNotificationRequest eventNotificationRequest, Long delay, TimeUnit timeUnit) {
        if (delay != null && delay > 0) {
            try {
                log.info("[SONAL] Sending event notification request for event : {}, user :{}", eventNotificationRequest.getEventName(), eventNotificationRequest.getUserId());
                scheduledThreadPoolExecutor.schedule(() -> this.doSend(eventNotificationRequest), delay, timeUnit);
            }
            catch (Exception e) {
                log.error("Error in scheduling send event notification for request: {}. Exception is: ", eventNotificationRequest, e);
            }
        }
        else
            this.doSend(eventNotificationRequest);
    }

    private void doSend(EventNotificationRequest eventNotificationRequest) {
        try {
            CompletableFuture<SendResult<String, EventNotificationRequest>> future = eventNotificationKafkaTemplate.send(eventNotificationTopic, eventNotificationRequest);
            log.info("[SONAL] Finally sending event notification request for event : {}, user :{}", eventNotificationRequest.getEventName(), eventNotificationRequest.getUserId());
            future.whenComplete(new BiConsumer<SendResult<String, EventNotificationRequest>, Throwable>() {
                @Override
                public void accept(SendResult<String, EventNotificationRequest> stringEventNotificationRequestSendResult, Throwable throwable) {
                    log.info(stringEventNotificationRequestSendResult.getProducerRecord().key()+ " info");
                    log.info(throwable.getMessage()+ " throw");
                }
            });
        }
        catch (Exception e) {
            log.error("Cannot connect to kafka producer. EventNotificationRequest: {} failed. Exception is:", eventNotificationRequest, e);
        }
    }
}
