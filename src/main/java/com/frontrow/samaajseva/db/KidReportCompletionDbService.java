package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.*;
import com.frontrow.samaajseva.enums.ChapterType;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.KidReportCompletionMapper;
import com.frontrow.samaajseva.pojo.request.MarkTopicAsCompleteRequest;
import com.frontrow.samaajseva.pojo.view.KidReportCompletionView;
import com.frontrow.samaajseva.repository.ChapterRepository;
import com.frontrow.samaajseva.repository.KidReportCompletionRepository;
import com.frontrow.samaajseva.repository.KidRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class KidReportCompletionDbService {

    private final ChapterRepository chapterRepository;
    private final KidRepository kidRepository;
    private final KidReportCompletionRepository kidReportCompletionRepository;
    private final KidReportCompletionMapper kidReportCompletionMapper;

    @Autowired
    public KidReportCompletionDbService(ChapterRepository chapterRepository,
                                        KidRepository kidRepository,
                                        KidReportCompletionRepository kidReportCompletionRepository,
                                        KidReportCompletionMapper kidReportCompletionMapper) {
        this.chapterRepository = chapterRepository;
        this.kidRepository = kidRepository;
        this.kidReportCompletionRepository = kidReportCompletionRepository;
        this.kidReportCompletionMapper = kidReportCompletionMapper;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markReportAsComplete(MarkTopicAsCompleteRequest markTopicAsCompleteRequest, Long updatedBy) {
        KidReportCompletion kidReportCompletion = kidReportCompletionRepository.getByKidIdAndChapterId(markTopicAsCompleteRequest.getKidId(), markTopicAsCompleteRequest.getChapterId());
        if(kidReportCompletion != null)
            return;

        Chapter chapter = chapterRepository.getByIdAndIsDeleted(markTopicAsCompleteRequest.getChapterId(), false);
        if(chapter == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
        if(ChapterType.LESSON.equals(chapter.getChapterType()))
            return;

        Kid kid = kidRepository.findById(markTopicAsCompleteRequest.getKidId()).orElse(null);

        kidReportCompletion = new KidReportCompletion();
        NullAwareBeanUtils.copyPropertiesWithoutNull(markTopicAsCompleteRequest, kidReportCompletion);
        kidReportCompletion.setChapter(chapter);
        kidReportCompletion.setKid(kid);
        kidReportCompletion.setUpdatedBy(updatedBy.toString());

        kidReportCompletionRepository.save(kidReportCompletion);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidReportCompletionView> getCompletedReport(List<Long> ids, List<Long> chapterIds, List<Long> kidIds, List<Long> sessionIds, List<SessionType> sessionTypes, Date createdAfter, Date createdBefore, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        QKidReportCompletion qKidReportCompletion = QKidReportCompletion.kidReportCompletion;
        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qKidReportCompletion.id.in(ids));

        if(!CollectionUtils.isEmpty(chapterIds))
            booleanExpression = booleanExpression.and(qKidReportCompletion.chapterId.in(chapterIds));

        if(!CollectionUtils.isEmpty(kidIds))
            booleanExpression = booleanExpression.and(qKidReportCompletion.kidId.in(kidIds));

        if(!CollectionUtils.isEmpty(sessionIds))
            booleanExpression = booleanExpression.and(qKidReportCompletion.sessionId.in(sessionIds));

        if(!CollectionUtils.isEmpty(sessionTypes))
            booleanExpression = booleanExpression.and(qKidReportCompletion.sessionType.in(sessionTypes));

        if(createdAfter != null)
            booleanExpression = booleanExpression.and(qKidReportCompletion.createdAt.after(createdAfter));

        if(createdBefore != null)
            booleanExpression = booleanExpression.and(qKidReportCompletion.createdAt.before(createdBefore));


        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<KidReportCompletion> kidReportCompletionList = pageable == null ?
                kidReportCompletionRepository.findAll(booleanExpression)
                : kidReportCompletionRepository.findAll(booleanExpression, pageable).getContent();

        return kidReportCompletionMapper.getViewListFromEntityList(kidReportCompletionList);
    }
}
