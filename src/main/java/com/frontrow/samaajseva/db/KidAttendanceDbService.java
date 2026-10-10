package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.KidAttendance;
import com.frontrow.samaajseva.entity.QKidAttendance;
import com.frontrow.samaajseva.enums.Attendance;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.mapper.KidAttendanceMapper;
import com.frontrow.samaajseva.pojo.response.KidAttendedResponse;
import com.frontrow.samaajseva.pojo.response.KidAttendedResponseView;
import com.frontrow.samaajseva.pojo.response.KidSessionMembershipResponse;
import com.frontrow.samaajseva.pojo.view.KidAttendanceView;
import com.frontrow.samaajseva.repository.KidAttendanceRepository;
import com.frontrow.samaajseva.repository.KidRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.Constants;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KidAttendanceDbService {

    private final KidAttendanceRepository kidAttendanceRepository;
    private final KidRepository kidRepository;
    private final KidAttendanceMapper kidAttendanceMapper;
    private final MembershipDbService membershipDbService;
    private final ThreadPoolTaskExecutor updateMembershipStartDateExecutor;

    @Autowired
    public KidAttendanceDbService(KidAttendanceRepository kidAttendanceRepository,
                                  KidRepository kidRepository,
                                  KidAttendanceMapper kidAttendanceMapper,
                                  MembershipDbService membershipDbService,
                                  @Qualifier("updateMembershipStartDateExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.kidAttendanceRepository = kidAttendanceRepository;
        this.kidRepository = kidRepository;
        this.kidAttendanceMapper = kidAttendanceMapper;
        this.membershipDbService = membershipDbService;
        this.updateMembershipStartDateExecutor = threadPoolTaskExecutor;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidAttendanceView> getByFilter(List<Long> kidIds, List<Long> sessionIds, List<SessionType> sessionTypes, Attendance attendance, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        QKidAttendance qKidAttendance = QKidAttendance.kidAttendance;
        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        if(!CollectionUtils.isEmpty(kidIds))
            booleanExpression = booleanExpression.and(qKidAttendance.kidId.in(kidIds));

        if(!CollectionUtils.isEmpty(sessionIds))
            booleanExpression = booleanExpression.and(qKidAttendance.sessionId.in(sessionIds));

        if(!CollectionUtils.isEmpty(sessionTypes))
            booleanExpression = booleanExpression.and(qKidAttendance.sessionType.in(sessionTypes));

        if(attendance != null)
            booleanExpression = booleanExpression.and(qKidAttendance.attendance.eq(attendance));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<KidAttendance> kidAttendanceList =  pageable == null ?
                kidAttendanceRepository.findAll(booleanExpression)
                : kidAttendanceRepository.findAll(booleanExpression, pageable).getContent();

        return kidAttendanceMapper.getViewListFromEntityList(kidAttendanceList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidAttendanceView> markKidsAttendance(List<Long> kidIds, Long sessionId, SessionType sessionType, Long updatedBy) {
        //is kid valid, is session valid, is kid part of the batch, membership active/inactive data
        List<KidSessionMembershipResponse> kidSessionMembershipResponseList = kidRepository.getSessionMembershipsData(kidIds, sessionId);
        List<KidAttendance> kidAttendanceList = kidAttendanceRepository.getByKidIdInAndSessionIdAndSessionType(kidIds, sessionId, sessionType);
        Map<Long, KidAttendance> kidIdAttendanceMap = new HashMap<>();

        List<KidAttendance> kidAttendanceSaveList = new ArrayList<>();
        Map<Long, Integer> unusedMembershipIdValidityMap = new HashMap<>();
        LocalDate sessionDate = null;

        if(!CollectionUtils.isEmpty(kidAttendanceList)) {
            kidIdAttendanceMap = kidAttendanceList.stream().collect(Collectors.toMap(KidAttendance::getKidId, Function.identity()));
        }

        if(!CollectionUtils.isEmpty(kidSessionMembershipResponseList)) {
            kidSessionMembershipResponseList = kidSessionMembershipResponseList.stream().filter(response -> sessionType.equals(response.getSessionType())).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(kidSessionMembershipResponseList)) {
                for(KidSessionMembershipResponse kidSessionMembershipResponse : kidSessionMembershipResponseList) {
                    sessionDate = kidSessionMembershipResponse.getSessionDate();
                    if(kidIdAttendanceMap.containsKey(kidSessionMembershipResponse.getKidId()) && !kidIdAttendanceMap.get(kidSessionMembershipResponse.getKidId()).getAttendance().equals(Attendance.PRESENT)) {
                        KidAttendance kidAttendance = kidIdAttendanceMap.get(kidSessionMembershipResponse.getKidId());
                        kidAttendance.setAttendance(Attendance.PRESENT);
                        kidAttendance.setUpdatedBy(updatedBy.toString());
                        kidAttendanceSaveList.add(kidAttendance);

                    } else if (!kidIdAttendanceMap.containsKey(kidSessionMembershipResponse.getKidId())) {
                        KidAttendance kidAttendance = new KidAttendance();
                        kidAttendance.setKidId(kidSessionMembershipResponse.getKidId());
                        kidAttendance.setSessionId(sessionId);
                        kidAttendance.setSessionType(sessionType);
                        kidAttendance.setAttendance(Attendance.PRESENT);
                        kidAttendance.setUpdatedBy(updatedBy.toString());

                        kidAttendanceSaveList.add(kidAttendance);
                    }

                    if(kidSessionMembershipResponse.getActiveMembershipId() == null
                            && kidSessionMembershipResponse.getUnusedMembershipId() != null
                            && SessionType.BATCH_SESSION.equals(sessionType)) {
                        unusedMembershipIdValidityMap.put(kidSessionMembershipResponse.getUnusedMembershipId(), kidSessionMembershipResponse.getValidityInMonths());
                    }
                }
            }
        }

        kidAttendanceRepository.saveAll(kidAttendanceSaveList);

        try {
            if(sessionDate != null) {
                LocalDate finalSessionDate = sessionDate;
                CompletableFuture.runAsync(() -> membershipDbService.updateMembershipDates(unusedMembershipIdValidityMap, finalSessionDate), updateMembershipStartDateExecutor);
            }
        } catch (Exception e) {
            log.error("Error while submitting task to the update membership start date executor");
        }

        return kidAttendanceMapper.getViewListFromEntityList(kidAttendanceSaveList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markAbsent(List<Long> kidIds, Long sessionId, SessionType sessionType, Long updatedBy) {
        kidAttendanceRepository.markAsAbsent(sessionId, sessionType.toString(), kidIds, updatedBy.toString());

        try{
            if(SessionType.BATCH_SESSION.equals(sessionType))
                CompletableFuture.runAsync(() -> membershipDbService.clearMembershipStartDate(kidIds, sessionId), updateMembershipStartDateExecutor);

        } catch (Exception e) {
            log.error("Error while submitting task to clear membership start date executor");
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidAttendedResponseView> getKidAttendanceBetweenDates(Long kidId, Long categoryId, LocalDate startDateAfter, LocalDate startDateBefore) {
        List<KidAttendedResponse> kidAttendedResponseList = kidAttendanceRepository.getSessionAttendanceBetweenDates(kidId, categoryId, startDateAfter, startDateBefore);
        List<KidAttendedResponseView> kidAttendedResponseViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(kidAttendedResponseList)){
            for(KidAttendedResponse kidAttendedResponse : kidAttendedResponseList){
                KidAttendedResponseView kidAttendedResponseView = new KidAttendedResponseView();
                NullAwareBeanUtils.copyPropertiesWithoutNull(kidAttendedResponse, kidAttendedResponseView);
                if(kidAttendedResponseView.getAttendance() == null && kidAttendedResponseView.getSessionDate().compareTo(LocalDate.now()) <= 0)
                    kidAttendedResponseView.setAttendance(Attendance.ABSENT);

                kidAttendedResponseViewList.add(kidAttendedResponseView);
            }
        }

        return kidAttendedResponseViewList;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void populateAbsenteeism() {
        List<KidAttendedResponse> kidAttendedResponseList = kidAttendanceRepository.getSessionAttendanceResponse(LocalDate.now());
        List<KidAttendance> kidAttendanceList = new ArrayList<>();
        List<Long> cancelledAttendanceIds = new ArrayList<>();

        if(!CollectionUtils.isEmpty(kidAttendedResponseList)) {
            for(KidAttendedResponse kidAttendedResponse : kidAttendedResponseList) {
                if(kidAttendedResponse.getIsSessionCancelled()) {
                    if(kidAttendedResponse.getAttendanceId() != null) {
                        cancelledAttendanceIds.add(kidAttendedResponse.getAttendanceId());

                    } else {
                        KidAttendance kidAttendance = new KidAttendance();

                        NullAwareBeanUtils.copyPropertiesWithoutNull(kidAttendedResponse, kidAttendance);
                        kidAttendance.setAttendance(Attendance.CANCELLED);
                        kidAttendance.setUpdatedBy(Constants.SYSTEM);

                        kidAttendanceList.add(kidAttendance);
                    }

                } else if(kidAttendedResponse.getAttendanceId() == null) {
                    KidAttendance kidAttendance = new KidAttendance();

                    NullAwareBeanUtils.copyPropertiesWithoutNull(kidAttendedResponse, kidAttendance);
                    kidAttendance.setAttendance(Attendance.ABSENT);
                    kidAttendance.setUpdatedBy(Constants.SYSTEM);

                    kidAttendanceList.add(kidAttendance);
                }
            }
        }

        kidAttendanceRepository.markCancelled(cancelledAttendanceIds);
        kidAttendanceRepository.saveAll(kidAttendanceList);
    }
}
