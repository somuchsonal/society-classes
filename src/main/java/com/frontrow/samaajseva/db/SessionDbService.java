package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.config.properties.EventNotificationProperties;
import com.frontrow.samaajseva.config.properties.MiscProperties;
import com.frontrow.samaajseva.entity.*;
import com.frontrow.samaajseva.enums.CancellationReason;
import com.frontrow.samaajseva.enums.Domain;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.kafka.producer.EventNotificationProducer;
import com.frontrow.samaajseva.mapper.SessionMapper;
import com.frontrow.samaajseva.pojo.request.EventNotificationRequest;
import com.frontrow.samaajseva.pojo.request.UpdateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.response.PopulateBatchSessionResponse;
import com.frontrow.samaajseva.pojo.response.PopulateBatchSessionSerializable;
import com.frontrow.samaajseva.pojo.view.BatchScheduleView;
import com.frontrow.samaajseva.pojo.view.KidView;
import com.frontrow.samaajseva.pojo.view.SessionView;
import com.frontrow.samaajseva.repository.BatchRepository;
import com.frontrow.samaajseva.repository.SessionRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.Constants;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class SessionDbService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final BatchRepository batchRepository;
    private final MiscProperties miscProperties;
    private final MembershipDbService membershipDbService;
    private final KidDbService kidDbService;
    private final EventNotificationProperties eventNotificationProperties;
    private final EventNotificationProducer eventNotificationProducer;
    private final ThreadPoolTaskExecutor extendMembershipExecutor;
    private final ThreadPoolTaskExecutor classCancellationNotificationExecutor;
    private final ThreadPoolTaskExecutor instructorDelayNotificationExecutor;

    @Autowired
    public SessionDbService(SessionRepository sessionRepository,
                            SessionMapper sessionMapper,
                            BatchRepository batchRepository,
                            MiscProperties miscProperties,
                            MembershipDbService membershipDbService,
                            KidDbService kidDbService,
                            EventNotificationProperties eventNotificationProperties,
                            EventNotificationProducer eventNotificationProducer,
                            @Qualifier("extendMembershipExecutor") ThreadPoolTaskExecutor extendMembershipExecutor,
                            @Qualifier("classCancellationNotificationExecutor") ThreadPoolTaskExecutor classCancellationNotificationExecutor,
                            @Qualifier("instructorDelayNotificationExecutor") ThreadPoolTaskExecutor instructorDelayNotificationExecutor) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
        this.batchRepository = batchRepository;
        this.miscProperties = miscProperties;
        this.membershipDbService = membershipDbService;
        this.kidDbService = kidDbService;
        this.eventNotificationProperties = eventNotificationProperties;
        this.eventNotificationProducer = eventNotificationProducer;
        this.extendMembershipExecutor = extendMembershipExecutor;
        this.classCancellationNotificationExecutor = classCancellationNotificationExecutor;
        this.instructorDelayNotificationExecutor = instructorDelayNotificationExecutor;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<SessionView> getByFilter(List<Long> ids, LocalDate startDateAfter, LocalDate startDateBefore, List<Long> batchIds, List<Long> instructorIds, List<String> venues, Boolean isCancelled, List<CancellationReason> cancellationReasons, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        QSession qSession = QSession.session;
        QBatch qBatch = QBatch.batch;

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qSession.id.in(ids));

        if(!CollectionUtils.isEmpty(batchIds))
            booleanExpression = booleanExpression.and(qSession.batchId.in(batchIds));

        if(startDateAfter != null)
            booleanExpression = booleanExpression.and(qSession.startDate.after(startDateAfter));

        if(startDateBefore != null)
            booleanExpression = booleanExpression.and(qSession.startDate.before(startDateBefore));

        if(!CollectionUtils.isEmpty(venues))
            booleanExpression = booleanExpression.and(qSession.venue.in(venues));

        if(isCancelled != null) {
            booleanExpression = booleanExpression.and(qSession.isCancelled.eq(isCancelled));

            if(!CollectionUtils.isEmpty(cancellationReasons))
                booleanExpression = booleanExpression.and(qSession.cancellationReason.in(cancellationReasons));
        }

        if(!CollectionUtils.isEmpty(instructorIds))
            booleanExpression = booleanExpression.and(qSession.batchId.in(JPAExpressions.select(qBatch.id)
                                                                                        .from(qBatch)
                                                                                        .where(qBatch.instructorId.in(instructorIds))));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Session> sessionList =  pageable == null ?
                sessionRepository.findAll(booleanExpression)
                : sessionRepository.findAll(booleanExpression, pageable).getContent();

        return sessionMapper.getViewListFromEntityList(sessionList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void populateBatchSessions() {
        List<PopulateBatchSessionResponse> populateBatchSessionResponseList = sessionRepository.getBatchesToBePopulated();
        List<PopulateBatchSessionSerializable> populateBatchSessionSerializableList = this.getSerializableResponse(populateBatchSessionResponseList);

        List<Session> sessionList = this.getSessionsToBePopulated(populateBatchSessionSerializableList);

        sessionRepository.saveAll(sessionList);
    }

    private List<Session> getSessionsToBePopulated(List<PopulateBatchSessionSerializable> populateBatchSessionSerializableList) {
        List<Session> sessionList = new ArrayList<>();

        for(PopulateBatchSessionSerializable populateBatchSessionSerializable : populateBatchSessionSerializableList){
            LocalDate populateBatchSessionTill = this.getDateToBePopulatedTill(populateBatchSessionSerializable);

            if(populateBatchSessionSerializable.getPopulatedTill() == null ||
                    populateBatchSessionSerializable.getPopulatedTill().isBefore(populateBatchSessionTill)) {

                LocalDate startDate = populateBatchSessionSerializable.getPopulatedTill() == null ?
                                                LocalDate.now().with(TemporalAdjusters.nextOrSame(populateBatchSessionSerializable.getDayOfWeek())) :
                                                populateBatchSessionSerializable.getPopulatedTill().with(TemporalAdjusters.next(populateBatchSessionSerializable.getDayOfWeek()));

                Batch batch = populateBatchSessionSerializable.getBatch();

                if(batch == null)
                    batch = batchRepository.getByIdAndIsDeleted(populateBatchSessionSerializable.getBatchId(), false);

                while(startDate.isBefore(populateBatchSessionTill)){
                    Session session = new Session();
                    session.setBatch(batch);
                    session.setStartDate(startDate);
                    NullAwareBeanUtils.copyPropertiesWithoutNull(populateBatchSessionSerializable, session);
                    session.setUpdatedBy(Constants.SYSTEM);

                    sessionList.add(session);

                    startDate = startDate.with(TemporalAdjusters.next(populateBatchSessionSerializable.getDayOfWeek()));
                }
            }
        }

        return sessionList;
    }

    private LocalDate getDateToBePopulatedTill(PopulateBatchSessionSerializable populateBatchSessionSerializable) {
        Calendar calendar = Calendar.getInstance();
        Date populateTillDate = DateUtils.addMonths(populateBatchSessionSerializable.getScheduleCreatedAt(), miscProperties.getPopulateSessionsForMonths());

        if(populateBatchSessionSerializable.getMembershipsValidTill() != null){
            populateTillDate = DateUtils.addMonths(Date.from(populateBatchSessionSerializable.getMembershipsValidTill().atStartOfDay(ZoneId.systemDefault()).toInstant()), 1);
            
        } else if(populateBatchSessionSerializable.getMembershipsValidForMonths() != null && populateBatchSessionSerializable.getKidsAddedAt() != null){
            populateTillDate = DateUtils.addMonths(populateBatchSessionSerializable.getKidsAddedAt(), populateBatchSessionSerializable.getMembershipsValidForMonths());
            
        }

        calendar.setTime(populateTillDate);

        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDate);

        return calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void populateSessionsForBatch(Batch batch) {
        List<PopulateBatchSessionSerializable> populateBatchSessionSerializableList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(batch.getBatchScheduleList())){
            for(BatchSchedule batchSchedule : batch.getBatchScheduleList()){
                PopulateBatchSessionSerializable populateBatchSessionSerializable = new PopulateBatchSessionSerializable();
                populateBatchSessionSerializable.setBatch(batch);
                populateBatchSessionSerializable.setInstructorId(batch.getInstructorId());
                populateBatchSessionSerializable.setScheduleCreatedAt(batchSchedule.getCreatedAt());
                NullAwareBeanUtils.copyPropertiesWithoutNull(batchSchedule, populateBatchSessionSerializable);

                populateBatchSessionSerializableList.add(populateBatchSessionSerializable);
            }
        }

        List<Session> sessionList = this.getSessionsToBePopulated(populateBatchSessionSerializableList);

        sessionRepository.saveAll(sessionList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markSessionAsCancelled(Long sessionId, LocalDate sessionDate, Long batchId, CancellationReason cancellationReason, String holidayName, Long updatedBy) {
        int updated = sessionRepository.markAsCancelled(sessionId, sessionDate, batchId, cancellationReason.toString(), updatedBy.toString());
        if(updated != 1)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        try {
            if(!CancellationReason.GAZETTED_HOLIDAY.equals(cancellationReason)) {
                CompletableFuture.runAsync(() -> membershipDbService.extendMembershipForCancellation(batchId, sessionDate), extendMembershipExecutor);
                CompletableFuture.runAsync(() -> this.sendFrontrowSessionCancellationNotification(batchId, cancellationReason, sessionDate), classCancellationNotificationExecutor);
            
            } else {
                CompletableFuture.runAsync(() -> this.sendHolidaySessionCancellationNotification(batchId, sessionDate, holidayName), classCancellationNotificationExecutor);
                
            }
        } catch (Exception e) {
            log.error("Error while submitting task to extend membership executor : {}", e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void sendHolidaySessionCancellationNotification(Long batchId, LocalDate sessionDate, String holidayName) {
        List<KidView> kidViews = kidDbService.getActiveKidParentIdsFromBatch(batchId);

        Batch batch = batchRepository.getByIdAndIsDeleted(batchId, false);
        if(batch.getSocietyId() != 3)
            return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if(!CollectionUtils.isEmpty(kidViews)){
            for (KidView kidView : kidViews){
                var templateData = new HashMap<String, String>();
                templateData.put("1", Optional.ofNullable(holidayName).orElse("a gazetted holiday"));
                templateData.put("2", kidView.getFirstName());
                templateData.put("3", formatter.format(sessionDate));

                EventNotificationRequest eventNotificationRequest = EventNotificationRequest.builder()
                                                                                            .eventName(eventNotificationProperties.getHolidaySessionCancellationEventName())
                                                                                            .userId(kidView.getParentUserId())
                                                                                            .templateData(templateData)
                                                                                            .overrideUserPreference(false)
                                                                                            .domain(Domain.FRONTROW.name())
                                                                                            .isOtpRequest(false)
                                                                                            .build();

                log.info("[SONAL] Sending holiday cancellation notifications for event : {}, kid : {}", eventNotificationRequest.getEventName(), kidView.getId());
                eventNotificationProducer.send(eventNotificationRequest, null, null);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void sendFrontrowSessionCancellationNotification(Long batchId, CancellationReason cancellationReason, LocalDate sessionDate) {
        List<KidView> kidViews = kidDbService.getActiveKidParentIdsFromBatch(batchId);

        Batch batch = batchRepository.getByIdAndIsDeleted(batchId, false);
        if(batch.getSocietyId() != 3)
            return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if(!CollectionUtils.isEmpty(kidViews)){
            for (KidView kidView : kidViews){
                var templateData = new HashMap<String, String>();
                templateData.put("CancellationReason", cancellationReason.getReasonText());
                templateData.put("Date", formatter.format(sessionDate));
                templateData.put("kidName", kidView.getFirstName());

                EventNotificationRequest eventNotificationRequest = EventNotificationRequest.builder()
                                                                                            .eventName(eventNotificationProperties.getFrontrowSessionCancellationEventName())
                                                                                            .userId(kidView.getParentUserId())
                                                                                            .templateData(templateData)
                                                                                            .overrideUserPreference(false)
                                                                                            .domain(Domain.FRONTROW.name())
                                                                                            .isOtpRequest(false)
                                                                                            .build();

                log.info("[SONAL] Sending fr cancellation notifications for event : {}, kid : {}", eventNotificationRequest.getEventName(), kidView.getId());
                eventNotificationProducer.send(eventNotificationRequest, null, null);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateInstructorId(Long batchId, Long instructorId) {
        sessionRepository.updateSessionInstructor(batchId, instructorId);
    }

    private List<PopulateBatchSessionSerializable> getSerializableResponse(List<PopulateBatchSessionResponse> populateBatchSessionResponseList) {
        List<PopulateBatchSessionSerializable> populateBatchSessionSerializableList = new ArrayList<>();

        if(populateBatchSessionResponseList != null){
            for(PopulateBatchSessionResponse populateBatchSessionResponse : populateBatchSessionResponseList){
                PopulateBatchSessionSerializable populateBatchSessionSerializable = new PopulateBatchSessionSerializable();
                NullAwareBeanUtils.copyPropertiesWithoutNull(populateBatchSessionResponse, populateBatchSessionSerializable);
                populateBatchSessionSerializableList.add(populateBatchSessionSerializable);
            }
        }

        return populateBatchSessionSerializableList;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateSessionSchedule(UpdateBatchScheduleRequest updateBatchScheduleRequest, BatchScheduleView oldBatchScheduleView) {
        List<Session> sessionList = sessionRepository.getByBatchIdAndStartTimeAndEndTimeAndIsCancelledAndStartDateAfter(updateBatchScheduleRequest.getBatchId(),
                                                                                                                        oldBatchScheduleView.getStartTime(),
                                                                                                                        oldBatchScheduleView.getEndTime(),
                                                                                                                        false, LocalDate.now());
        List<Session> updatedSessionsList = new ArrayList<>();

        if(sessionList == null)
            return;

        LocalDate nextSessionDate = null;
        if(updateBatchScheduleRequest.getDayOfWeek() != null && !updateBatchScheduleRequest.getDayOfWeek().equals(oldBatchScheduleView.getDayOfWeek()))
             nextSessionDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(updateBatchScheduleRequest.getDayOfWeek()));

        boolean populatedNextAvailableDate = false ;

        for(Session session : sessionList){
            //Comparing day of week, start time and end time to check if the session matches this schedule, because these fields have unique constraint for each batch
            if(session.getStartDate().getDayOfWeek().equals(oldBatchScheduleView.getDayOfWeek())){
                NullAwareBeanUtils.copyPropertiesWithoutNull(updateBatchScheduleRequest, session);

                //if next session date is null, that means that day of week is not updated
                if(nextSessionDate != null){
                    LocalDate newStartDate = session.getStartDate().with(TemporalAdjusters.next(updateBatchScheduleRequest.getDayOfWeek()));
                    session.setStartDate(newStartDate);

                    if(newStartDate.equals(nextSessionDate))
                        populatedNextAvailableDate = true;
                }

                session.setUpdatedBy(Constants.SYSTEM);
                updatedSessionsList.add(session);
            }
        }

        if(updateBatchScheduleRequest.getPopulateAnyGapDay() && nextSessionDate != null && !populatedNextAvailableDate){
            Session session = new Session();
            Session updatedSession = updatedSessionsList.get(0);

            session.setStartDate(nextSessionDate);
            session.setBatch(updatedSession.getBatch());
            session.setStartTime(updatedSession.getStartTime());
            session.setEndTime(updatedSession.getEndTime());
            session.setInstructorId(updatedSession.getInstructorId());
            session.setVenue(updatedSession.getVenue());

            session.setUpdatedBy(Constants.SYSTEM);

            updatedSessionsList.add(session);
        }

        sessionRepository.saveAll(updatedSessionsList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteSessionsFromSchedule(BatchSchedule batchSchedule, Boolean deleteTodaysSession) {
        LocalDate deleteFrom = deleteTodaysSession ? LocalDate.now().minusDays(1) : LocalDate.now();

        List<Session> sessionList = sessionRepository.getByBatchIdAndStartTimeAndEndTimeAndStartDateAfter(batchSchedule.getBatchId(), batchSchedule.getStartTime(), batchSchedule.getEndTime(), deleteFrom);
        List<Session> sessionDeleteList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(sessionList)){
            for(Session session : sessionList){
                if(session.getStartDate().getDayOfWeek().equals(batchSchedule.getDayOfWeek()))
                    sessionDeleteList.add(session);
            }
        }

        sessionRepository.deleteAll(sessionDeleteList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteSessionsByBatch(Long batchId, Boolean deleteTodaysSession) {
        LocalDate deleteFrom = deleteTodaysSession ? LocalDate.now().minusDays(1) : LocalDate.now();

        sessionRepository.deleteByBatchIdAndIsCancelledAndStartDateAfter(batchId, false, deleteFrom);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markDelay(Long sessionId, Integer minutesDelayed, Long updatedBy) {
        Session session = sessionRepository.getByIdAndIsCancelled(sessionId, false);
        if(session == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        session.setMinutesDelayed(Optional.ofNullable(session.getMinutesDelayed()).orElse(0) + minutesDelayed);
        session.setUpdatedBy(updatedBy.toString());

        sessionRepository.save(session);

        try {
            CompletableFuture.runAsync(() -> this.sendInstructorDelayNotification(session.getBatchId(), session.getMinutesDelayed(), session.getStartTime()), instructorDelayNotificationExecutor);

        } catch (Exception e) {
            log.error("Error in submitting task to instructor delay notification executor");
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void sendInstructorDelayNotification(Long batchId, Integer minutesDelayed, LocalTime startTime) {
        List<KidView> kids = kidDbService.getActiveKidParentIdsFromBatch(batchId);

        Batch batch = batchRepository.getByIdAndIsDeleted(batchId, false);
        if(batch.getSocietyId() != 3)
            return;

        if(!CollectionUtils.isEmpty(kids)){
            for (KidView kidView : kids){
                var templateData = new HashMap<String, String>();
                templateData.put("delay", minutesDelayed.toString());
                templateData.put("kidName",kidView.getFirstName());
                templateData.put("StartTime", startTime.toString());

                EventNotificationRequest eventNotificationRequest = EventNotificationRequest.builder()
                                                                                            .eventName(eventNotificationProperties.getInstructorDelayEventName())
                                                                                            .userId(kidView.getParentUserId())
                                                                                            .templateData(templateData)
                                                                                            .overrideUserPreference(false)
                                                                                            .domain(Domain.FRONTROW.name())
                                                                                            .isOtpRequest(false)
                                                                                            .build();

                log.info("[SONAL] Sending delay notifications for event : {}, kid : {}", eventNotificationRequest.getEventName(), kidView.getId());
                eventNotificationProducer.send(eventNotificationRequest, null, null);
            }
        }

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeExtraSessions() {
        List<PopulateBatchSessionResponse> populateBatchSessionResponseList = sessionRepository.getBatchesToBeCleaned();
        List<PopulateBatchSessionSerializable> populateBatchSessionSerializableList = this.getSerializableResponse(populateBatchSessionResponseList);

        Map<Long, LocalDate> batchIdPopulateTillDate = new HashMap<>();

        for(PopulateBatchSessionSerializable populateBatchSessionSerializable : populateBatchSessionSerializableList){
            batchIdPopulateTillDate.put(populateBatchSessionSerializable.getBatchId(), this.getDateToBePopulatedTill(populateBatchSessionSerializable));
        }

        List<Session> sessionDeleteList = new ArrayList<>();

        for (Map.Entry<Long, LocalDate> entry : batchIdPopulateTillDate.entrySet()){
            List<Session> batchSessions = sessionRepository.getByBatchIdAndIsCancelledAndStartDateAfter(entry.getKey(), false, entry.getValue());

            if(!CollectionUtils.isEmpty(batchSessions))
                sessionDeleteList.addAll(batchSessions);
        }

        if(!CollectionUtils.isEmpty(sessionDeleteList))
            sessionRepository.deleteAll(sessionDeleteList);
    }
}
