package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.config.properties.EventNotificationProperties;
import com.frontrow.samaajseva.entity.*;
import com.frontrow.samaajseva.enums.Domain;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.kafka.producer.EventNotificationProducer;
import com.frontrow.samaajseva.pojo.request.EventNotificationRequest;
import com.frontrow.samaajseva.repository.BatchKidMappingRepository;
import com.frontrow.samaajseva.repository.BatchRepository;
import com.frontrow.samaajseva.repository.KidRepository;
import com.frontrow.samaajseva.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BatchKidMappingDbService {

    private final BatchRepository batchRepository;
    private final KidRepository kidRepository;
    private final BatchKidMappingRepository batchKidMappingRepository;
    private final MembershipDbService membershipDbService;
    private final EventNotificationProperties eventNotificationProperties;
    private final EventNotificationProducer eventNotificationProducer;
    private final ThreadPoolTaskExecutor batchKidEnrollmentNotificationExecutor;

    @Autowired
    public BatchKidMappingDbService(BatchRepository batchRepository,
                                    KidRepository kidRepository,
                                    BatchKidMappingRepository batchKidMappingRepository,
                                    MembershipDbService membershipDbService,
                                    EventNotificationProperties eventNotificationProperties,
                                    EventNotificationProducer eventNotificationProducer,
                                    @Qualifier("batchKidEnrollmentNotificationExecutor") ThreadPoolTaskExecutor batchKidEnrollmentNotificationExecutor) {
        this.batchRepository = batchRepository;
        this.kidRepository = kidRepository;
        this.batchKidMappingRepository = batchKidMappingRepository;
        this.membershipDbService = membershipDbService;
        this.eventNotificationProperties = eventNotificationProperties;
        this.eventNotificationProducer = eventNotificationProducer;
        this.batchKidEnrollmentNotificationExecutor = batchKidEnrollmentNotificationExecutor;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addKidToBatch(Long kidId, Long batchId, Long updatedBy) {
        Long kidsBatchId = batchKidMappingRepository.findByKidIdAndCategoryId(kidId, batchId);

        if(Objects.equals(kidsBatchId, batchId))
            throw new CustomException(ErrorCodes.KID_ALREADY_ADDED_TO_BATCH);

        if(kidsBatchId != null)
            batchKidMappingRepository.deleteByKidIdAndBatchId(kidId, kidsBatchId);

        BatchKidMapping batchKidMapping = new BatchKidMapping();

        Kid kid = kidRepository.findById(kidId).orElse(null);
        if(kid == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        Batch batch = batchRepository.getByIdAndIsDeleted(batchId, false);
        if(batch == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        Membership membership = membershipDbService.getLastActiveMembership(kidId, batch.getCategoryId(), batch.getSocietyId());

        if(membership == null || (membership.getEndDate() != null && membership.getEndDate().isBefore(LocalDate.now())))
            throw new CustomException(ErrorCodes.NO_ACTIVE_MEMBERSHIP);

        if(membership.getCategoryId() != batch.getCategoryId())
            throw new CustomException(ErrorCodes.INSTRUCTOR_CATEGORY_MISMATCH);

        batchKidMapping.setKid(kid);
        batchKidMapping.setBatch(batch);
        batchKidMapping.setUpdatedBy(updatedBy.toString());

        batchKidMappingRepository.save(batchKidMapping);

        try {
            CompletableFuture.runAsync(() -> this.sendBatchKidEnrollmentNotification(batch, kid), batchKidEnrollmentNotificationExecutor);

        } catch (Exception e) {
            log.error("Error while submitting task to batch kid enrollment notification executor");
        }

    }

    private void sendBatchKidEnrollmentNotification(Batch batch, Kid kid) {
        if(CollectionUtils.isEmpty(batch.getBatchScheduleList()) || batch.getSocietyId() != 3)
            return;

        String eventName = eventNotificationProperties.getBatchKidEnrolmentSingleScheduleEventName();

        var templateData = new HashMap<String, String>();

        templateData.put("KidName", kid.getFirstName());
        templateData.put("day1", batch.getBatchScheduleList().get(0).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        templateData.put("time1", batch.getBatchScheduleList().get(0).getStartTime() + " - " + batch.getBatchScheduleList().get(0).getEndTime());
        templateData.put("venue1", batch.getBatchScheduleList().get(0).getVenue());

        if(batch.getBatchScheduleList().size() > 1) {
            eventName = eventNotificationProperties.getBatchKidEnrolmentTwoSchedulesEventName();

            templateData.put("day2", batch.getBatchScheduleList().get(1).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            templateData.put("time2", batch.getBatchScheduleList().get(1).getStartTime() + " - " + batch.getBatchScheduleList().get(1).getEndTime());
            templateData.put("venue2", batch.getBatchScheduleList().get(1).getVenue());
        }

        EventNotificationRequest eventNotificationRequest = EventNotificationRequest.builder()
                                                                                    .eventName(eventName)
                                                                                    .userId(kid.getParentUserId())
                                                                                    .domain(Domain.FRONTROW.name())
                                                                                    .isOtpRequest(false)
                                                                                    .templateData(templateData)
                                                                                    .overrideUserPreference(false)
                                                                                    .build();

        log.info("[SONAL] Sending batch enrolment notifications for event : {}, kid : {}", eventName, kid.getId());
        eventNotificationProducer.send(eventNotificationRequest, null, null);
    }

    private String getBatchScheduleText(Batch batch) {
        StringBuilder stringBuilder = new StringBuilder();

        if(!CollectionUtils.isEmpty(batch.getBatchScheduleList())){
            List<String> venues = batch.getBatchScheduleList().stream().map(BatchSchedule::getVenue).distinct().toList();

            for(BatchSchedule batchSchedule : batch.getBatchScheduleList()){
                stringBuilder.append(Constants.DAY).append(Constants.SEPARATOR)
                             .append(batchSchedule.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                             .append(System.lineSeparator());

                stringBuilder.append(Constants.TIME).append(Constants.SEPARATOR)
                             .append(batchSchedule.getStartTime())
                             .append(Constants.SEPARATOR)
                             .append(batchSchedule.getEndTime())
                             .append(System.lineSeparator());

                if(venues.size() > 1) {
                    stringBuilder.append(Constants.VENUE).append(Constants.SEPARATOR)
                                 .append(batchSchedule.getVenue())
                                 .append(System.lineSeparator());
                }

                stringBuilder.append(System.lineSeparator());
            }

            if(venues.size() == 1) {
                stringBuilder.append(Constants.VENUE).append(Constants.SEPARATOR)
                             .append(venues.get(0))
                             .append(System.lineSeparator());

                stringBuilder.append(System.lineSeparator());
            }
        }

        return stringBuilder.toString();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeKidFromBatch(Long kidId, Long batchId) {
        batchKidMappingRepository.deleteByKidIdAndBatchId(kidId, batchId);
    }
}
