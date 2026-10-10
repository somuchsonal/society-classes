package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.*;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.VideoMapper;
import com.frontrow.samaajseva.pojo.request.CreateVideoRequest;
import com.frontrow.samaajseva.pojo.view.VideoView;
import com.frontrow.samaajseva.repository.SessionRepository;
import com.frontrow.samaajseva.repository.VideoRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
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

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class VideoDbService {

    private final VideoRepository videoRepository;
    private final SessionRepository sessionRepository;
    private final VideoMapper videoMapper;
    private final VideoKidMappingDbService videoKidMappingDbService;
    private final ThreadPoolTaskExecutor createVideoKidMappingExecutor;

    @Autowired
    public VideoDbService(VideoRepository videoRepository,
                          SessionRepository sessionRepository,
                          VideoMapper videoMapper,
                          VideoKidMappingDbService videoKidMappingDbService,
                          @Qualifier("createVideoKidMappingExecutor") ThreadPoolTaskExecutor createVideoKidMappingExecutor) {
        this.videoRepository = videoRepository;
        this.sessionRepository = sessionRepository;
        this.videoMapper = videoMapper;
        this.videoKidMappingDbService = videoKidMappingDbService;
        this.createVideoKidMappingExecutor = createVideoKidMappingExecutor;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<VideoView> getByFilter(List<Long> ids, List<Long> updatedByIds, Date createdAfter, Date createdBefore, List<Long> sessionIds, List<Long> batchIds, List<Long> kidIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        QVideo qVideo = QVideo.video;
        QSession qSession = QSession.session;
        QVideoKidMapping qVideoKidMapping = QVideoKidMapping.videoKidMapping;

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qVideo.id.in(ids));

        if(!CollectionUtils.isEmpty(updatedByIds)) {
            List<String> updatedByIdStrings = updatedByIds.stream().map(Object::toString).toList();
            booleanExpression = booleanExpression.and(qVideo.updatedBy.in(updatedByIdStrings));
        }

        if(createdAfter != null)
            booleanExpression = booleanExpression.and(qVideo.createdAt.after(createdAfter));

        if(createdBefore != null)
            booleanExpression = booleanExpression.and(qVideo.createdAt.before(createdBefore));

        if(!CollectionUtils.isEmpty(sessionIds))
            booleanExpression = booleanExpression.and(qVideo.sessionId.in(sessionIds));

        if(!CollectionUtils.isEmpty(batchIds))
            booleanExpression = booleanExpression.and(qVideo.sessionId.in(JPAExpressions.select(qSession.id).from(qSession).where(qSession.batchId.in(batchIds))));

        if(!CollectionUtils.isEmpty(kidIds))
            booleanExpression = booleanExpression.and(qVideo.id.in(JPAExpressions.select(qVideoKidMapping.videoId).from(qVideoKidMapping).where(qVideoKidMapping.kidId.in(kidIds))));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Video> videoList = pageable == null ?
                videoRepository.findAll(booleanExpression)
                : videoRepository.findAll(booleanExpression, pageable).getContent();

        return videoMapper.getViewListFromEntityList(videoList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public VideoView create(CreateVideoRequest createVideoRequest, Long updatedBy) {
        Session session = null;
        if(createVideoRequest.getSessionId() != null) {
            session = sessionRepository.getByIdAndIsCancelled(createVideoRequest.getSessionId(), false);

            if(session == null)
                throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
        }

        Video video = new Video();

        NullAwareBeanUtils.copyPropertiesWithoutNull(createVideoRequest, video);
        video.setUpdatedBy(updatedBy.toString());
        if(session != null)
            video.setSession(session);

        video = videoRepository.save(video);

        try {
            Video finalVideo = video;
            CompletableFuture.runAsync(() -> videoKidMappingDbService.createWithVideo(finalVideo, createVideoRequest.getKidIds(), updatedBy), createVideoKidMappingExecutor);

        } catch (Exception e) {
            log.error("Error in submitting task to create video kid mapping executor : {}", e.getMessage());
        }

        return videoMapper.getViewFromEntity(video);
    }
}
