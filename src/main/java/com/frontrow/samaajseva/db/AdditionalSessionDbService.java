package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.AdditionalSession;
import com.frontrow.samaajseva.entity.QAdditionalSession;
import com.frontrow.samaajseva.entity.QAdditionalSessionKidMapping;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.AdditionalSessionMapper;
import com.frontrow.samaajseva.pojo.request.CreateAdditionalSessionRequest;
import com.frontrow.samaajseva.pojo.request.UpdateAdditionalSessionRequest;
import com.frontrow.samaajseva.pojo.response.InstructorSessionConflictResponse;
import com.frontrow.samaajseva.pojo.view.AdditionalSessionView;
import com.frontrow.samaajseva.repository.AdditionalSessionRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class AdditionalSessionDbService {

    private final AdditionalSessionRepository additionalSessionRepository;
    private final AdditionalSessionMapper additionalSessionMapper;

    @Autowired
    public AdditionalSessionDbService(AdditionalSessionRepository additionalSessionRepository,
                                      AdditionalSessionMapper additionalSessionMapper) {
        this.additionalSessionRepository = additionalSessionRepository;
        this.additionalSessionMapper = additionalSessionMapper;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<AdditionalSessionView> getByFilter(List<Long> ids, List<Long> instructorIds, LocalDate startDateAfter, LocalDate startDateBefore, List<Long> categoryIds, Boolean isCancelled, List<Long> kidIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        QAdditionalSession qAdditionalSession = QAdditionalSession.additionalSession;
        QAdditionalSessionKidMapping qAdditionalSessionKidMapping = QAdditionalSessionKidMapping.additionalSessionKidMapping;

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qAdditionalSession.id.in(ids));

        if(!CollectionUtils.isEmpty(instructorIds))
            booleanExpression = booleanExpression.and(qAdditionalSession.instructorId.in(instructorIds));

        if(startDateAfter != null)
            booleanExpression = booleanExpression.and(qAdditionalSession.startDate.after(startDateAfter));

        if(startDateBefore != null)
            booleanExpression = booleanExpression.and(qAdditionalSession.startDate.before(startDateBefore));

        if(!CollectionUtils.isEmpty(categoryIds))
            booleanExpression = booleanExpression.and(qAdditionalSession.categoryId.in(categoryIds));

        if(isCancelled != null)
            booleanExpression = booleanExpression.and(qAdditionalSession.isCancelled.eq(isCancelled));

        if(!CollectionUtils.isEmpty(kidIds))
            booleanExpression = booleanExpression.and(qAdditionalSession.id.in(JPAExpressions.select(qAdditionalSessionKidMapping.additionalSessionId)
                                                                                             .from(qAdditionalSessionKidMapping)
                                                                                             .where(qAdditionalSessionKidMapping.kidId.in(kidIds))));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<AdditionalSession> additionalSessionList =  pageable == null ?
                additionalSessionRepository.findAll(booleanExpression)
                : additionalSessionRepository.findAll(booleanExpression, pageable).getContent();

        return additionalSessionMapper.getViewListFromEntityList(additionalSessionList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AdditionalSessionView create(CreateAdditionalSessionRequest createAdditionalSessionRequest, Long updatedBy) {
        AdditionalSession additionalSession = new AdditionalSession();

        NullAwareBeanUtils.copyPropertiesWithoutNull(createAdditionalSessionRequest, additionalSession);
        additionalSession.setUpdatedBy(updatedBy.toString());

        this.checkConflictingSchedules(additionalSession);

        additionalSessionRepository.save(additionalSession);

        return additionalSessionMapper.getViewFromEntity(additionalSession);
    }

    private void checkConflictingSchedules(AdditionalSession additionalSession) {
        List<InstructorSessionConflictResponse> instructorSessionConflictResponses = additionalSessionRepository.getConflictingSessions(additionalSession.getInstructorId(), additionalSession.getStartDate(), additionalSession.getStartTime(), additionalSession.getEndTime());

        if(!CollectionUtils.isEmpty(instructorSessionConflictResponses)){
            if(additionalSession.getId() == null || instructorSessionConflictResponses.size() > 1)
                throw new CustomException(ErrorCodes.INSTRUCTOR_CONFLICTS_IN_SCHEDULE);

            if(!(instructorSessionConflictResponses.get(0).getSessionId().equals(additionalSession.getId()) && SessionType.ADDITIONAL_SESSION.equals(instructorSessionConflictResponses.get(0).getSessionType())))
                throw new CustomException(ErrorCodes.INSTRUCTOR_CONFLICTS_IN_SCHEDULE);
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AdditionalSessionView update(UpdateAdditionalSessionRequest updateAdditionalSessionRequest, Long updatedBy) {
        AdditionalSession additionalSession = additionalSessionRepository.getByIdAndIsCancelled(updateAdditionalSessionRequest.getId(), false);
        if(additionalSession == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        boolean scheduleChanged = this.hasScheduleChanges(updateAdditionalSessionRequest, additionalSession);

        NullAwareBeanUtils.copyPropertiesWithoutNull(updateAdditionalSessionRequest, additionalSession);
        additionalSession.setUpdatedBy(updatedBy.toString());

        if(!additionalSession.getStartTime().isBefore(additionalSession.getEndTime()))
            throw new CustomException(ErrorCodes.START_TIME_SHOULD_BE_BEFORE_END_TIME);

        if(scheduleChanged)
            this.checkConflictingSchedules(additionalSession);

        additionalSessionRepository.save(additionalSession);

        return additionalSessionMapper.getViewFromEntity(additionalSession);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markAsCancelled(Long id, Long updatedBy) {
        additionalSessionRepository.markAsCancelled(id, updatedBy.toString());
    }

    private boolean hasScheduleChanges(UpdateAdditionalSessionRequest updateAdditionalSessionRequest, AdditionalSession additionalSession) {
        return (updateAdditionalSessionRequest.getStartDate() != null && updateAdditionalSessionRequest.getStartDate() != additionalSession.getStartDate())
            || (updateAdditionalSessionRequest.getEndTime() != null && updateAdditionalSessionRequest.getEndTime() != additionalSession.getEndTime())
            || (updateAdditionalSessionRequest.getStartTime() != null && updateAdditionalSessionRequest.getStartTime() != additionalSession.getStartTime());

    }
}
