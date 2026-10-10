package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.Chapter;
import com.frontrow.samaajseva.entity.QChapter;
import com.frontrow.samaajseva.entity.QTopic;
import com.frontrow.samaajseva.entity.Topic;
import com.frontrow.samaajseva.enums.CompletionType;
import com.frontrow.samaajseva.enums.ContentType;
import com.frontrow.samaajseva.enums.Level;
import com.frontrow.samaajseva.enums.TopicType;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.TopicMapper;
import com.frontrow.samaajseva.pojo.request.CreateTopicRequest;
import com.frontrow.samaajseva.pojo.request.UpdateTopicRequest;
import com.frontrow.samaajseva.pojo.view.TopicView;
import com.frontrow.samaajseva.repository.ChapterRepository;
import com.frontrow.samaajseva.repository.TopicRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class TopicDbService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;
    private final ChapterRepository chapterRepository;

    @Autowired
    public TopicDbService(TopicRepository topicRepository,
                          TopicMapper topicMapper,
                          ChapterRepository chapterRepository) {
        this.topicRepository = topicRepository;
        this.topicMapper = topicMapper;
        this.chapterRepository = chapterRepository;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<TopicView> getByFilter(List<Long> ids, List<TopicType> topicTypes, List<ContentType> contentTypes, List<CompletionType> completionTypes, List<Long> chapterIds, Boolean isDeleted, List<Long> categoryIds, List<Level> levels, List<Integer> subLevels, List<Integer> subSubLevels, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        QTopic qTopic = QTopic.topic;
        QChapter qChapter = QChapter.chapter;
        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qTopic.id.in(ids));

        if(!CollectionUtils.isEmpty(topicTypes))
            booleanExpression = booleanExpression.and(qTopic.topicType.in(topicTypes));

        if(!CollectionUtils.isEmpty(contentTypes))
            booleanExpression = booleanExpression.and(qTopic.contentType.in(contentTypes));

        if(!CollectionUtils.isEmpty(completionTypes))
            booleanExpression = booleanExpression.and(qTopic.completionType.in(completionTypes));

        if(!CollectionUtils.isEmpty(chapterIds))
            booleanExpression = booleanExpression.and(qTopic.chapterId.in(chapterIds));

        if(isDeleted != null)
            booleanExpression = booleanExpression.and(qTopic.isDeleted.eq(isDeleted));

        if(!CollectionUtils.isEmpty(categoryIds) && !CollectionUtils.isEmpty(levels) && !CollectionUtils.isEmpty(subLevels) && !CollectionUtils.isEmpty(subSubLevels)) {
            booleanExpression = booleanExpression.and(qTopic.chapterId.in(JPAExpressions.select(qChapter.id)
                                                                                        .from(qChapter)
                                                                                        .where(qChapter.categoryId.in(categoryIds)
                                                                                                .and(qChapter.level.in(levels))
                                                                                                .and(qChapter.subLevel.in(subLevels))
                                                                                                .and(qChapter.subSubLevel.in(subSubLevels)))));

        } else if(!CollectionUtils.isEmpty(categoryIds) && !CollectionUtils.isEmpty(levels) && !CollectionUtils.isEmpty(subLevels)) {
            booleanExpression = booleanExpression.and(qTopic.chapterId.in(JPAExpressions.select(qChapter.id)
                                                                                        .from(qChapter)
                                                                                        .where(qChapter.categoryId.in(categoryIds)
                                                                                                .and(qChapter.level.in(levels))
                                                                                                .and(qChapter.subLevel.in(subLevels)))));

        } else if(!CollectionUtils.isEmpty(categoryIds) && !CollectionUtils.isEmpty(levels)){
            booleanExpression = booleanExpression.and(qTopic.chapterId.in(JPAExpressions.select(qChapter.id)
                                                                                        .from(qChapter)
                                                                                        .where(qChapter.categoryId.in(categoryIds)
                                                                                                .and(qChapter.level.in(levels)))));

        } else if(!CollectionUtils.isEmpty(categoryIds)){
            booleanExpression = booleanExpression.and(qTopic.chapterId.in(JPAExpressions.select(qChapter.id)
                                                                                        .from(qChapter)
                                                                                        .where(qChapter.categoryId.in(categoryIds))));
        }

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Topic> topicList = pageable == null ?
                topicRepository.findAll(booleanExpression)
                : topicRepository.findAll(booleanExpression, pageable).getContent();

        return topicMapper.getViewListFromEntityList(topicList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public TopicView create(CreateTopicRequest createTopicRequest, Long updatedBy) {
        Chapter chapter = chapterRepository.getByIdAndIsDeleted(createTopicRequest.getChapterId(), false);
        if(chapter == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        Topic topic = new Topic();
        NullAwareBeanUtils.copyPropertiesWithoutNull(createTopicRequest,topic);
        topic.setChapter(chapter);
        topic.setUpdatedBy(updatedBy.toString());

        topicRepository.save(topic);

        return topicMapper.getViewFromEntity(topic);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public TopicView update(UpdateTopicRequest updateTopicRequest, Long updatedBy) {
        Topic topic = topicRepository.getByIdAndIsDeleted(updateTopicRequest.getTopicId(), false);
        if(topic == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        if(updateTopicRequest.getChapterId() != null && !updateTopicRequest.getChapterId().equals(topic.getChapterId())){
            Chapter chapter = chapterRepository.getByIdAndIsDeleted(updateTopicRequest.getChapterId(), false);
            if(chapter == null)
                throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

            topic.setChapter(chapter);
        }

        NullAwareBeanUtils.copyPropertiesWithoutNull(updateTopicRequest, topic);
        topic.setUpdatedBy(updatedBy.toString());

        topicRepository.save(topic);

        return topicMapper.getViewFromEntity(topic);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Long getTopicCategory(Long topicId) {
        return topicRepository.getTopicCategory(topicId);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markAsDeleted(Long id, Long updatedBy) {
        topicRepository.markAsDeleted(id, updatedBy.toString());
    }
}
