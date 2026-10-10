package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.ChapterDbService;
import com.frontrow.samaajseva.db.KidReportCompletionDbService;
import com.frontrow.samaajseva.db.KidTopicTrackingDbService;
import com.frontrow.samaajseva.db.TopicDbService;
import com.frontrow.samaajseva.enums.ChapterType;
import com.frontrow.samaajseva.enums.CompletionType;
import com.frontrow.samaajseva.enums.Level;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.TopicMetadata;
import com.frontrow.samaajseva.pojo.request.CreateChapterRequest;
import com.frontrow.samaajseva.pojo.request.UpdateChapterRequest;
import com.frontrow.samaajseva.pojo.view.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChapterService {

    private final ChapterDbService chapterDbService;
    private final TopicDbService topicDbService;
    private final KidTopicTrackingDbService kidTopicTrackingDbService;
    private final KidReportCompletionDbService kidReportCompletionDbService;

    @Autowired
    public ChapterService(ChapterDbService chapterDbService,
                          TopicDbService topicDbService,
                          KidTopicTrackingDbService kidTopicTrackingDbService,
                          KidReportCompletionDbService kidReportCompletionDbService) {
        this.chapterDbService = chapterDbService;
        this.topicDbService = topicDbService;
        this.kidTopicTrackingDbService = kidTopicTrackingDbService;
        this.kidReportCompletionDbService = kidReportCompletionDbService;
    }

    public List<ChapterView> getByFilter(List<Long> ids, List<Long> categoryIds, List<Level> levels, List<Integer> subLevels, List<Integer> subSubLevels, List<ChapterType> chapterTypes, Boolean isDeleted, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return chapterDbService.getByFilter(ids, categoryIds, levels, subLevels, subSubLevels, chapterTypes, isDeleted, pageSize, pageNumber, sortColumn, sortOrder);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ChapterView create(CreateChapterRequest createChapterRequest, Long updatedBy) {
        return chapterDbService.create(createChapterRequest, updatedBy);
    }

    public ChapterView update(UpdateChapterRequest updateChapterRequest, Long updatedBy) {
        return chapterDbService.update(updateChapterRequest, updatedBy);
    }

    public void delete(Long id, Long updatedBy) {
        chapterDbService.markAsDeleted(id, updatedBy);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidReportCompletionView> getCompletedReport(List<Long> ids, List<Long> chapterIds, List<Long> kidIds, List<Long> sessionIds, List<SessionType> sessionTypes, Date createdAfter, Date createdBefore, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        //Getting kid report completion at chapter level
        List<KidReportCompletionView> kidReportCompletionViewList = kidReportCompletionDbService.getCompletedReport(ids, chapterIds, kidIds, sessionIds, sessionTypes, createdAfter, createdBefore, pageSize, pageNumber, sortColumn, sortOrder);
        //Creating a map for kid+chapter, completion view
        Map<String, KidReportCompletionView> kidChapterCompletionMapping = this.getKidChapterCompletionMapping(kidReportCompletionViewList);

        //updating chapterIds, kidIds to only have the ones which are completed
        chapterIds = kidReportCompletionViewList.stream().map(KidReportCompletionView::getChapterView).map(ChapterView::getId).collect(Collectors.toList());
        kidIds = kidReportCompletionViewList.stream().map(KidReportCompletionView::getKidView).map(KidView::getId).collect(Collectors.toList());

        //Getting all topics of the completed chapter ids, then creating a topicId-chapter map and a chapterId-topic map
        List<TopicView> topicViewList = topicDbService.getByFilter(null, null, null, List.of(CompletionType.COMMENT, CompletionType.RATED), chapterIds, null, null, null, null, null, null, null, null, null);
        Map<Long, ChapterView> topicIdChapterMap = topicViewList.stream().collect(Collectors.toMap(TopicView::getId, TopicView::getChapterView));

        //Getting all completed kid topic completions
        List<KidTopicTrackingView> kidTopicTrackingViewList = kidTopicTrackingDbService.getCompletionByFilter(null, topicViewList.stream().map(TopicView::getId).collect(Collectors.toList()), kidIds, sessionIds, sessionTypes, null, null, null, null);

        //Populating topic completions inside chapter completion for each kid
        this.populateTopicCompletion(kidTopicTrackingViewList, kidChapterCompletionMapping, topicIdChapterMap);

        //Creating a chapterId-topics map and then populating the total number of stars for a chapter
        Map<Long, List<TopicView>> chapterIdTopicsMap = this.getChapterIdTopicsMap(topicViewList);
        this.populateTotalStars(kidReportCompletionViewList, chapterIdTopicsMap);

        return kidReportCompletionViewList;
     }

    private void populateTotalStars(List<KidReportCompletionView> kidReportCompletionViewList, Map<Long, List<TopicView>> chapterIdTopicsMap) {
        for(KidReportCompletionView kidReportCompletionView : kidReportCompletionViewList) {
            Integer totalStars = 0;

            var topicViewList = Optional.ofNullable(chapterIdTopicsMap.get(kidReportCompletionView.getChapterView().getId())).orElse(new ArrayList<>());

            var allRatingParameters = topicViewList.stream().map(TopicView::getMetadata).filter(Objects::nonNull).map(TopicMetadata::getRatingParameters).filter(Objects::nonNull).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(allRatingParameters)) {
                for(var ratingParameters : allRatingParameters) {
                    for(var entry : ratingParameters.entrySet())
                        totalStars += entry.getValue();
                }
            }

            kidReportCompletionView.setTotalStars(totalStars);
        }
    }

    private Map<Long, List<TopicView>> getChapterIdTopicsMap(List<TopicView> topicViewList) {
        var chapterIdTopicsMap = new HashMap<Long, List<TopicView>>();

        for (TopicView topicView : topicViewList) {
            if(!chapterIdTopicsMap.containsKey(topicView.getChapterView().getId()))
                chapterIdTopicsMap.put(topicView.getChapterView().getId(), new ArrayList<>());

            chapterIdTopicsMap.get(topicView.getChapterView().getId()).add(topicView);
        }

        return chapterIdTopicsMap;
    }

    private void populateTopicCompletion(List<KidTopicTrackingView> kidTopicTrackingViewList, Map<String, KidReportCompletionView> kidChapterCompletionMapping, Map<Long, ChapterView> topicIdChapterMap) {
        if(!CollectionUtils.isEmpty(kidTopicTrackingViewList)) {
            for(KidTopicTrackingView kidTopicTrackingView : kidTopicTrackingViewList) {
                KidReportCompletionView kidReportCompletionView = kidChapterCompletionMapping.get(kidTopicTrackingView.getKidView().getId() + "+" + Optional.ofNullable(topicIdChapterMap.get(kidTopicTrackingView.getTopicView().getId())).map(ChapterView::getId).orElse(0L));
                if(kidReportCompletionView == null)
                    continue;

                if(kidReportCompletionView.getKidTopicTrackingViewList() == null)
                    kidReportCompletionView.setKidTopicTrackingViewList(new ArrayList<>());

                kidReportCompletionView.getKidTopicTrackingViewList().add(kidTopicTrackingView);
            }
        }
    }

    private Map<String, KidReportCompletionView> getKidChapterCompletionMapping(List<KidReportCompletionView> kidReportCompletionViewList) {
        Map<String, KidReportCompletionView> kidChapterCompletionMapping = new HashMap<>();

        if(!CollectionUtils.isEmpty(kidReportCompletionViewList)) {
            for(KidReportCompletionView kidReportCompletionView : kidReportCompletionViewList) {
                kidChapterCompletionMapping.put(kidReportCompletionView.getKidView().getId() + "+" + kidReportCompletionView.getChapterView().getId(), kidReportCompletionView);
            }
        }

        return kidChapterCompletionMapping;
    }
}
