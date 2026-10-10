package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.config.properties.EventNotificationProperties;
import com.frontrow.samaajseva.entity.*;
import com.frontrow.samaajseva.enums.Domain;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.kafka.producer.EventNotificationProducer;
import com.frontrow.samaajseva.mapper.MembershipMapper;
import com.frontrow.samaajseva.pojo.request.EventNotificationRequest;
import com.frontrow.samaajseva.pojo.request.SocietyMembershipPurchaseEvent;
import com.frontrow.samaajseva.pojo.response.SessionResponse;
import com.frontrow.samaajseva.pojo.view.MembershipView;
import com.frontrow.samaajseva.repository.*;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.Constants;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MembershipDbService {

    private final MembershipRepository membershipRepository;
    private final MembershipMapper membershipMapper;
    private final SocietyRepository societyRepository;
    private final BatchRepository batchRepository;
    private final KidRepository kidRepository;
    private final SessionRepository sessionRepository;
    private final KidAttendanceRepository kidAttendanceRepository;
    private final EventNotificationProperties eventNotificationProperties;
    private final EventNotificationProducer eventNotificationProducer;
    private final ThreadPoolTaskExecutor userOnboardedNotificationExecutor;

    @Autowired
    public MembershipDbService(MembershipRepository membershipRepository,
                               MembershipMapper membershipMapper,
                               SocietyRepository societyRepository,
                               BatchRepository batchRepository,
                               KidRepository kidRepository,
                               SessionRepository sessionRepository,
                               KidAttendanceRepository kidAttendanceRepository,
                               EventNotificationProperties eventNotificationProperties,
                               EventNotificationProducer eventNotificationProducer,
                               @Qualifier("userOnboardedNotificationExecutor") ThreadPoolTaskExecutor userOnboardedNotificationExecutor) {
        this.membershipRepository = membershipRepository;
        this.membershipMapper = membershipMapper;
        this.societyRepository = societyRepository;
        this.batchRepository = batchRepository;
        this.kidRepository = kidRepository;
        this.sessionRepository = sessionRepository;
        this.kidAttendanceRepository = kidAttendanceRepository;
        this.eventNotificationProperties = eventNotificationProperties;
        this.eventNotificationProducer = eventNotificationProducer;
        this.userOnboardedNotificationExecutor = userOnboardedNotificationExecutor;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<MembershipView> getByFilter(List<Long> ids, List<Long> kidIds, List<Long> categoryIds, List<Long> societyIds, Integer validityInMonths, List<Long> orderIds, Boolean isPartlyPaid, LocalDate startDateAfter, LocalDate startDateBefore, LocalDate endDateAfter, LocalDate endDateBefore, Boolean hasNotStarted, List<Long> parentUserIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        QMembership qMembership = QMembership.membership;
        QKid qKid = QKid.kid;
        
        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        
        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qMembership.id.in(ids));
        
        if(!CollectionUtils.isEmpty(kidIds))
            booleanExpression = booleanExpression.and(qMembership.kidId.in(kidIds));
        
        if(!CollectionUtils.isEmpty(categoryIds))
            booleanExpression = booleanExpression.and(qMembership.categoryId.in(categoryIds));
        
        if(!CollectionUtils.isEmpty(societyIds))
            booleanExpression = booleanExpression.and(qMembership.societyId.in(societyIds));
        
        if(validityInMonths != null)
            booleanExpression = booleanExpression.and(qMembership.validityInMonths.eq(validityInMonths));

        if(!CollectionUtils.isEmpty(orderIds))
            booleanExpression = booleanExpression.and(qMembership.orderId.in(orderIds));

        if(isPartlyPaid != null)
            booleanExpression = booleanExpression.and(qMembership.isPartlyPaid.eq(isPartlyPaid));
        
        if(startDateAfter != null)
            booleanExpression = booleanExpression.and(qMembership.startDate.after(startDateAfter));
        
        if(startDateBefore != null)
            booleanExpression = booleanExpression.and(qMembership.startDate.before(startDateBefore));
        
        if(endDateAfter != null)
            booleanExpression = booleanExpression.and(qMembership.endDate.after(endDateAfter));

        if(endDateBefore != null)
            booleanExpression = booleanExpression.and(qMembership.endDate.before(endDateBefore));

        if(hasNotStarted != null)
            booleanExpression = booleanExpression.and(qMembership.endDate.isNull().and(qMembership.startDate.isNull()));

        if(!CollectionUtils.isEmpty(parentUserIds))
            booleanExpression = booleanExpression.and(qMembership.kidId.in(JPAExpressions.select(qKid.id)
                                                                                         .from(qKid)
                                                                                         .where(qKid.parentUserId.in(parentUserIds))));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Membership> membershipList =  pageable == null ?
                membershipRepository.findAll(booleanExpression)
                : membershipRepository.findAll(booleanExpression, pageable).getContent();

        return membershipMapper.getViewListFromEntityList(membershipList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public MembershipView create(SocietyMembershipPurchaseEvent societyMembershipPurchaseEvent) {
        Kid kid = kidRepository.findById(societyMembershipPurchaseEvent.getKidId()).orElse(null);
        if(kid == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        List<Membership> membershipList = membershipRepository.getByKidIdAndCategoryId(societyMembershipPurchaseEvent.getKidId(), societyMembershipPurchaseEvent.getCategoryId());

        Membership membership = null;
        try{
            membership = membershipList.stream().filter(mem -> mem.getOrderId().equals(societyMembershipPurchaseEvent.getOrderId())).toList().get(0);

        } catch (Exception e) {}

        if(membership == null) {
            membership = new Membership();

            Society society = societyRepository.findById(societyMembershipPurchaseEvent.getSocietyId()).orElse(null);
            if(society == null)
                throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
            membership.setSociety(society);

            NullAwareBeanUtils.copyPropertiesWithoutNull(societyMembershipPurchaseEvent, membership);
            membership.setKid(kid);

        } else {
            if(membership.getEndDate() != null)
                membership.setEndDate(membership.getEndDate().plusMonths(societyMembershipPurchaseEvent.getValidityInMonths()));

            membership.setValidityInMonths(membership.getValidityInMonths() + societyMembershipPurchaseEvent.getValidityInMonths());
            membership.setIsPartlyPaid(societyMembershipPurchaseEvent.getIsPartlyPaid());
        }

        membership.setUpdatedBy(Constants.SYSTEM);

        membershipRepository.save(membership);

        try {
            if(CollectionUtils.isEmpty(membershipList) && societyMembershipPurchaseEvent.getSocietyId() == 3)
                CompletableFuture.runAsync(() -> this.sendKidOnboardingMessage(kid.getFirstName(), kid.getParentUserId()), userOnboardedNotificationExecutor);

        } catch (Exception e){
            log.error("Error in submitting to send onboarding notification executor due to : {}", e.getMessage());
        }

        return membershipMapper.getViewFromEntity(membership);
    }

    private void sendKidOnboardingMessage(String firstName, Long parentUserId) {
        var templateData = new HashMap<String, String>();

        templateData.put("KidName", firstName);

        EventNotificationRequest eventNotificationRequest = EventNotificationRequest.builder()
                                                                                    .eventName(eventNotificationProperties.getSocietyUserOnboardingEventName())
                                                                                    .templateData(templateData)
                                                                                    .domain(Domain.FRONTROW.name())
                                                                                    .overrideUserPreference(false)
                                                                                    .userId(parentUserId)
                                                                                    .isOtpRequest(false)
                                                                                    .build();

        log.info("[SONAL] Sending onboarding notifications for event : {}, kid : {}", eventNotificationRequest.getEventName(), firstName);
        eventNotificationProducer.send(eventNotificationRequest, null, null);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateMembershipDates(Map<Long, Integer> updateMemberships, LocalDate sessionDate) {
        List<Long> membershipIds = new ArrayList<>(updateMemberships.keySet());
        if(CollectionUtils.isEmpty(membershipIds))
            return;

        List<Membership> memberships = membershipRepository.getByIdIn(membershipIds);

        for(Membership membership : memberships){
            membership.setStartDate(sessionDate);
            membership.setEndDate(sessionDate.plusMonths(updateMemberships.get(membership.getId())));
            membership.setUpdatedBy(Constants.SYSTEM);
        }

        membershipRepository.saveAll(memberships);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void extendMembershipForCancellation(Long batchId, LocalDate sessionDate) {
        Batch batch = batchRepository.getByIdAndIsDeleted(batchId, false);
        if(batch == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        if(CollectionUtils.isEmpty(batch.getBatchScheduleList()))
            return;

        QMembership qMembership = QMembership.membership;
        QBatchKidMapping qBatchKidMapping = QBatchKidMapping.batchKidMapping;
        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        booleanExpression = booleanExpression.and(qMembership.endDate.after(sessionDate));

        booleanExpression = booleanExpression.and(qMembership.kidId.in(JPAExpressions.select(qBatchKidMapping.kidId)
                                                                                     .from(qBatchKidMapping)
                                                                                     .where(qBatchKidMapping.batchId.eq(batchId))));

        List<Membership> membershipList = membershipRepository.findAll(booleanExpression);
        if(CollectionUtils.isEmpty(membershipList))
            return;

        for(Membership membership : membershipList) {
            LocalDate extendedDate = LocalDate.MAX;

            for(BatchSchedule batchSchedule : batch.getBatchScheduleList()){
                LocalDate tempExtendedDate = membership.getEndDate().with(TemporalAdjusters.next(batchSchedule.getDayOfWeek()));

                if(tempExtendedDate.isBefore(extendedDate))
                    extendedDate = tempExtendedDate;
            }

            membership.setEndDate(extendedDate);
        }

        membershipRepository.saveAll(membershipList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Membership getLastActiveMembership(Long kidId, Long categoryId, Long societyId) {
        var membershipList = membershipRepository.getByKidIdAndCategoryIdAndSocietyId(kidId, categoryId, societyId);

        if(CollectionUtils.isEmpty(membershipList))
            return null;

        membershipList.sort(Comparator.comparing(Membership::getCreatedAt));

        List<Membership> activeMemberships = membershipList.stream().filter(membership -> membership.getEndDate() != null && membership.getEndDate().isAfter(LocalDate.now())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(activeMemberships))
            return activeMemberships.get(activeMemberships.size() - 1);

        List<Membership> unusedMemberships = membershipList.stream().filter(membership -> membership.getEndDate() == null).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(unusedMemberships))
            return unusedMemberships.get(0);

        membershipList = membershipList.stream().filter(membership -> membership.getEndDate() != null).collect(Collectors.toList());
        Collections.sort(membershipList, Comparator.comparing(Membership::getEndDate));

        return membershipList.get(membershipList.size() - 1);
    }

    public void clearMembershipStartDate(List<Long> kidIds, Long sessionId) {
        SessionResponse sessionResponse = sessionRepository.getResponseById(sessionId);
        List<Membership> membershipList = membershipRepository.getByKidIdInAndCategoryIdAndStartDate(kidIds, sessionResponse.getCategoryId(), sessionResponse.getStartDate());

        if(!CollectionUtils.isEmpty(membershipList)) {
            for(Membership membership : membershipList) {
                SessionResponse sessionResponse1 = kidAttendanceRepository.getNextPresentDate(membership.getKidId(), sessionResponse.getBatchId(), sessionResponse.getStartDate());

                if(sessionResponse1 != null && sessionResponse1.getStartDate() != null) {
                    membership.setStartDate(sessionResponse1.getStartDate());
                    membership.setEndDate(membership.getStartDate().plusMonths(membership.getValidityInMonths()));

                } else {
                    membership.setStartDate(null);
                    membership.setEndDate(null);
                }
            }
        }

        membershipRepository.saveAll(membershipList);
    }
}
