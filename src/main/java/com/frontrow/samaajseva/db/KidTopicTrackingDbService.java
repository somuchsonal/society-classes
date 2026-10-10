package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.comparator.CurrentLevelResponseComparator;
import com.frontrow.samaajseva.entity.*;
import com.frontrow.samaajseva.enums.Level;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.KidTopicTrackingMapper;
import com.frontrow.samaajseva.pojo.request.MarkTopicAsCompleteRequest;
import com.frontrow.samaajseva.pojo.response.CompletionPercentageResponse;
import com.frontrow.samaajseva.pojo.response.CurrentLevelResponse;
import com.frontrow.samaajseva.pojo.view.CompletionPercentageView;
import com.frontrow.samaajseva.pojo.view.CurrentLevelView;
import com.frontrow.samaajseva.pojo.view.KidTopicTrackingView;
import com.frontrow.samaajseva.repository.KidRepository;
import com.frontrow.samaajseva.repository.KidTopicTrackingRepository;
import com.frontrow.samaajseva.repository.TopicRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KidTopicTrackingDbService {

    private final KidTopicTrackingRepository kidTopicTrackingRepository;
    private final KidTopicTrackingMapper kidTopicTrackingMapper;
    private final TopicRepository topicRepository;
    private final KidRepository kidRepository;
    private final KidReportCompletionDbService kidReportCompletionDbService;
    private final ThreadPoolTaskExecutor markReportCompletionExecutor;

    @Autowired
    public KidTopicTrackingDbService(KidTopicTrackingRepository kidTopicTrackingRepository,
                                     KidTopicTrackingMapper kidTopicTrackingMapper,
                                     TopicRepository topicRepository,
                                     KidRepository kidRepository,
                                     KidReportCompletionDbService kidReportCompletionDbService, @Qualifier("markReportCompletionExecutor") ThreadPoolTaskExecutor markReportCompletionExecutor) {
        this.kidTopicTrackingRepository = kidTopicTrackingRepository;
        this.kidTopicTrackingMapper = kidTopicTrackingMapper;
        this.topicRepository = topicRepository;
        this.kidRepository = kidRepository;
        this.kidReportCompletionDbService = kidReportCompletionDbService;
        this.markReportCompletionExecutor = markReportCompletionExecutor;
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markTopicAsComplete(MarkTopicAsCompleteRequest markTopicAsCompleteRequest, Long updatedBy) {
        KidTopicTracking kidTopicTracking = kidTopicTrackingRepository.getByKidIdAndTopicId(markTopicAsCompleteRequest.getKidId(), markTopicAsCompleteRequest.getTopicId());

        if(kidTopicTracking == null) {
            Kid kid = kidRepository.findById(markTopicAsCompleteRequest.getKidId()).orElse(null);
            if (kid == null)
                throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

            Topic topic = topicRepository.getByIdAndIsDeleted(markTopicAsCompleteRequest.getTopicId(), false);
            if (topic == null)
                throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

            kidTopicTracking = new KidTopicTracking();
            NullAwareBeanUtils.copyPropertiesWithoutNull(markTopicAsCompleteRequest, kidTopicTracking);
            kidTopicTracking.setKid(kid);
            kidTopicTracking.setTopic(topic);
            kidTopicTracking.setUpdatedBy(updatedBy.toString());

        } else {
            NullAwareBeanUtils.copyPropertiesWithoutNull(markTopicAsCompleteRequest, kidTopicTracking);
            kidTopicTracking.setUpdatedBy(updatedBy.toString());
        }

        try {
            if(markTopicAsCompleteRequest.getChapterId() != null) {
                CompletableFuture.runAsync(() -> kidReportCompletionDbService.markReportAsComplete(markTopicAsCompleteRequest, updatedBy), markReportCompletionExecutor);
            }
        } catch (Exception e) {
            log.error("Error while submitting task to the mark report completion executor");
        }

        kidTopicTrackingRepository.save(kidTopicTracking);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidTopicTrackingView> getCompletionByFilter(List<Long> ids, List<Long> topicIds, List<Long> kidIds, List<Long> sessionIds, List<SessionType> sessionTypes, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        QKidTopicTracking qKidTopicTracking = QKidTopicTracking.kidTopicTracking;

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qKidTopicTracking.id.in(ids));

        if(!CollectionUtils.isEmpty(topicIds))
            booleanExpression = booleanExpression.and(qKidTopicTracking.topicId.in(topicIds));

        if(!CollectionUtils.isEmpty(kidIds))
            booleanExpression = booleanExpression.and(qKidTopicTracking.kidId.in(kidIds));

        if(!CollectionUtils.isEmpty(sessionIds))
            booleanExpression = booleanExpression.and(qKidTopicTracking.sessionId.in(sessionIds));

        if(!CollectionUtils.isEmpty(sessionTypes))
            booleanExpression = booleanExpression.and(qKidTopicTracking.sessionType.in(sessionTypes));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<KidTopicTracking> kidTopicTrackingList = pageable == null ?
                kidTopicTrackingRepository.findAll(booleanExpression)
                : kidTopicTrackingRepository.findAll(booleanExpression, pageable).getContent();

        return kidTopicTrackingMapper.getViewListFromEntityList(kidTopicTrackingList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public CurrentLevelView getCurrentLevel(Long kidId, Long categoryId) {
        List<CurrentLevelResponse> currentLevelResponses = kidTopicTrackingRepository.getAllLevels(kidId, categoryId);

        if(CollectionUtils.isEmpty(currentLevelResponses))
            return new CurrentLevelView(Level.BEGINNER, 1, 0);

        currentLevelResponses.sort(new CurrentLevelResponseComparator());

        CurrentLevelView highestLevelResponse = new CurrentLevelView();
        NullAwareBeanUtils.copyPropertiesWithoutNull(currentLevelResponses.get(0), highestLevelResponse);

        return highestLevelResponse;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteTopicCompletion(Long kidId, Long topicId) {
        kidTopicTrackingRepository.deleteByKidIdAndTopicId(kidId, topicId);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public CompletionPercentageView getCompletionPercentage(Long kidId, Long categoryId) {
        List<CompletionPercentageResponse> completionPercentageResponses = kidTopicTrackingRepository.getCompletionPercentageResponses(kidId, categoryId);
        CompletionPercentageView completionPercentageView = new CompletionPercentageView();

        if(CollectionUtils.isEmpty(completionPercentageResponses)){
            return completionPercentageView;
        }

        Map<Level, Integer> completionPercentageMap = new HashMap<>();

        for(CompletionPercentageResponse completionPercentageResponse : completionPercentageResponses)
            completionPercentageMap.put(completionPercentageResponse.getLevel(), (completionPercentageResponse.getTotalCompletionCount() * 100) / completionPercentageResponse.getTotalTopicCount());

        completionPercentageView.setCompletionPercentageMap(completionPercentageMap);

        return completionPercentageView;
    }
}
