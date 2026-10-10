package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.cache.CompletionPercentageCache;
import com.frontrow.samaajseva.cache.CurrentLevelCache;
import com.frontrow.samaajseva.db.KidTopicTrackingDbService;
import com.frontrow.samaajseva.db.TopicDbService;
import com.frontrow.samaajseva.enums.*;
import com.frontrow.samaajseva.pojo.request.CreateTopicRequest;
import com.frontrow.samaajseva.pojo.request.MarkTopicAsCompleteRequest;
import com.frontrow.samaajseva.pojo.request.UpdateTopicRequest;
import com.frontrow.samaajseva.pojo.view.CompletionPercentageView;
import com.frontrow.samaajseva.pojo.view.CurrentLevelView;
import com.frontrow.samaajseva.pojo.view.KidTopicTrackingView;
import com.frontrow.samaajseva.pojo.view.TopicView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TopicService {

    private final TopicDbService topicDbService;
    private final KidTopicTrackingDbService kidTopicTrackingDbService;
    private final CurrentLevelCache currentLevelCache;
    private final CompletionPercentageCache completionPercentageCache;

    @Autowired
    public TopicService(TopicDbService topicDbService,
                        KidTopicTrackingDbService kidTopicTrackingDbService,
                        CurrentLevelCache currentLevelCache,
                        CompletionPercentageCache completionPercentageCache) {
        this.topicDbService = topicDbService;
        this.kidTopicTrackingDbService = kidTopicTrackingDbService;
        this.currentLevelCache = currentLevelCache;
        this.completionPercentageCache = completionPercentageCache;
    }

    public List<TopicView> getByFilter(List<Long> ids, List<TopicType> topicTypes, List<ContentType> contentTypes, List<CompletionType> completionTypes, List<Long> chapterIds, Boolean isDeleted, List<Long> categoryIds, List<Level> levels, List<Integer> subLevels, List<Integer> subSubLevels, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return topicDbService.getByFilter(ids, topicTypes, contentTypes, completionTypes, chapterIds, isDeleted, categoryIds, levels, subLevels, subSubLevels, pageSize, pageNumber, sortColumn, sortOrder);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public TopicView create(CreateTopicRequest createTopicRequest, Long updatedBy) {
        return topicDbService.create(createTopicRequest, updatedBy);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public TopicView update(UpdateTopicRequest updateTopicRequest, Long updatedBy) {
        return topicDbService.update(updateTopicRequest, updatedBy);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markTopicAsComplete(MarkTopicAsCompleteRequest markTopicAsCompleteRequest, Long updatedBy) {
        kidTopicTrackingDbService.markTopicAsComplete(markTopicAsCompleteRequest, updatedBy);

        Long categoryId = topicDbService.getTopicCategory(markTopicAsCompleteRequest.getTopicId());

        currentLevelCache.invalidateCache(markTopicAsCompleteRequest.getKidId(), categoryId);
        completionPercentageCache.invalidateCache(markTopicAsCompleteRequest.getKidId(), categoryId);
    }

    public List<KidTopicTrackingView> getCompletionByFilter(List<Long> ids, List<Long> topicIds, List<Long> kidIds, List<Long> sessionIds, List<SessionType> sessionTypes, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return kidTopicTrackingDbService.getCompletionByFilter(ids, topicIds, kidIds, sessionIds, sessionTypes, pageSize, pageNumber, sortColumn, sortOrder);
    }

    public CurrentLevelView getCurrentLevel(Long kidId, Long categoryId) {
        return currentLevelCache.getCurrentLevel(kidId, categoryId);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteTopicCompletion(Long kidId, Long topicId) {
        kidTopicTrackingDbService.deleteTopicCompletion(kidId, topicId);

        Long categoryId = topicDbService.getTopicCategory(topicId);

        currentLevelCache.invalidateCache(kidId, categoryId);
        completionPercentageCache.invalidateCache(kidId, categoryId);
    }

    public CompletionPercentageView getCompletionPercentage(Long kidId, Long categoryId) {
        return completionPercentageCache.getCompletionPercentage(kidId, categoryId);
    }

    public void delete(Long id, Long updatedBy) {
        topicDbService.markAsDeleted(id, updatedBy);
    }
}
