package com.frontrow.samaajseva.kafka.consumer;

import com.frontrow.samaajseva.pojo.request.SocietyMembershipPurchaseEvent;
import com.frontrow.samaajseva.service.MembershipService;
import com.frontrow.samaajseva.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SocietyMembershipPurchaseEventConsumer {

    @Value("${kafka.societyMembershipPurchase.topic}")
    private String societyMembershipPurchaseEventTopic;

    private final MembershipService membershipService;
    private final KafkaTemplate<String, SocietyMembershipPurchaseEvent> societyMembershipPurchaseEventRetryKafkaTemplate;

    @Autowired
    public SocietyMembershipPurchaseEventConsumer(MembershipService membershipService,
                                                  @Qualifier("societyMembershipPurchaseEventKafkaTemplate") KafkaTemplate<String, SocietyMembershipPurchaseEvent> societyMembershipPurchaseEventRetryKafkaTemplate) {
        this.membershipService = membershipService;
        this.societyMembershipPurchaseEventRetryKafkaTemplate = societyMembershipPurchaseEventRetryKafkaTemplate;
    }

    @KafkaListener(topics = "#{'${kafka.societyMembershipPurchase.topic}'}",
            containerFactory = "societyMembershipPurchaseEventKafkaListenerContainerFactory")
    public void consume(SocietyMembershipPurchaseEvent societyMembershipPurchaseEvent) {
        try {
            this.doOperation(societyMembershipPurchaseEvent);
        }
        catch (Exception e) {
            societyMembershipPurchaseEventRetryKafkaTemplate.send(societyMembershipPurchaseEventTopic.concat(Constants.KAFKA_RETRY_TOPIC_SUFFIX),
                                                                  societyMembershipPurchaseEvent);
            log.info("Consumer failed. Retrying for request: {}. Exceptions is: ", societyMembershipPurchaseEvent, e);
        }
    }

    @KafkaListener(topics = "#{'${kafka.societyMembershipPurchase.topic}'.concat('_RETRY')}",
            containerFactory = "societyMembershipPurchaseRetryKafkaListenerContainerFactory")
    public void retry(SocietyMembershipPurchaseEvent societyMembershipPurchaseEvent) throws Exception {
        try {
            this.doOperation(societyMembershipPurchaseEvent);
        }
        catch (Exception e) {
            log.error("Retry Consumer failed. Retrying again for request: {}. Exceptions is: ", societyMembershipPurchaseEvent, e);
            throw e;
        }
    }

    private void doOperation(SocietyMembershipPurchaseEvent societyMembershipPurchaseEvent) {
        membershipService.create(societyMembershipPurchaseEvent);
    }

}
