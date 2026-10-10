package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.*;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.BatchMapper;
import com.frontrow.samaajseva.pojo.request.CreateBatchRequest;
import com.frontrow.samaajseva.pojo.request.CreateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.request.UpdateBatchRequest;
import com.frontrow.samaajseva.pojo.view.BatchView;
import com.frontrow.samaajseva.repository.BatchRepository;
import com.frontrow.samaajseva.repository.SocietyRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BatchDbService {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final BatchScheduleDbService batchScheduleDbService;
    private final SocietyRepository societyRepository;
    private final SessionDbService sessionDbService;
    private final ThreadPoolTaskExecutor populateBatchSessionExecutor;
    private final ThreadPoolTaskExecutor updateSessionInstructorExecutor;
    private final ThreadPoolTaskExecutor deleteBatchScheduleExecutor;


    @Autowired
    public BatchDbService(BatchRepository batchRepository,
                          BatchMapper batchMapper,
                          BatchScheduleDbService batchScheduleDbService,
                          SocietyRepository societyRepository,
                          SessionDbService sessionDbService,
                          @Qualifier("updateSessionInstructorExecutor") ThreadPoolTaskExecutor updateSessionInstructorExecutor,
                          @Qualifier("populateBatchSessionExecutor") ThreadPoolTaskExecutor populateBatchSessionExecutor,
                          @Qualifier("deleteBatchScheduleExecutor") ThreadPoolTaskExecutor deleteBatchScheduleExecutor) {
        this.batchRepository = batchRepository;
        this.batchMapper = batchMapper;
        this.batchScheduleDbService = batchScheduleDbService;
        this.societyRepository = societyRepository;
        this.sessionDbService = sessionDbService;
        this.updateSessionInstructorExecutor = updateSessionInstructorExecutor;
        this.populateBatchSessionExecutor = populateBatchSessionExecutor;
        this.deleteBatchScheduleExecutor = deleteBatchScheduleExecutor;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<BatchView> getByFilter(List<Long> ids, List<Long> societyIds, List<Long> instructorIds, List<Long> categoryIds, List<Long> clusterIds, List<Long> cityIds, List<Long> kidIds, Boolean isDeleted, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        QBatch qBatch = QBatch.batch;
        QSociety qSociety = QSociety.society;
        QSociety qSociety1 = QSociety.society;
        QCluster qCluster = QCluster.cluster;
        QBatchKidMapping qBatchKidMapping = QBatchKidMapping.batchKidMapping;

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qBatch.id.in(ids));

        if(!CollectionUtils.isEmpty(societyIds))
            booleanExpression = booleanExpression.and(qBatch.societyId.in(societyIds));

        if(!CollectionUtils.isEmpty(instructorIds))
            booleanExpression = booleanExpression.and(qBatch.instructorId.in(instructorIds));

        if(!CollectionUtils.isEmpty(categoryIds))
            booleanExpression = booleanExpression.and(qBatch.categoryId.in(categoryIds));

        if(isDeleted != null)
            booleanExpression = booleanExpression.and(qBatch.isDeleted.eq(isDeleted));

        if(!CollectionUtils.isEmpty(kidIds))
            booleanExpression = booleanExpression.and(qBatch.id.in(JPAExpressions.select(qBatchKidMapping.batchId)
                                                                                 .from(qBatchKidMapping)
                                                                                 .where(qBatchKidMapping.kidId.in(kidIds))));

        if(!CollectionUtils.isEmpty(clusterIds))
            booleanExpression = booleanExpression.and(qBatch.societyId.in(JPAExpressions.select(qSociety.id)
                                                                                        .from(qSociety)
                                                                                        .where(qSociety.clusterId.in(clusterIds))));

        if(!CollectionUtils.isEmpty(cityIds))
            booleanExpression = booleanExpression.and(qBatch.societyId.in(JPAExpressions.select(qSociety1.id)
                                                                                        .from(qSociety1)
                                                                                        .where(qSociety1.clusterId.in(JPAExpressions.select(qCluster.id)
                                                                                                                                    .from(qCluster)
                                                                                                                                    .where(qCluster.cityId.in(cityIds))))));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Batch> batchList =  pageable == null ?
                batchRepository.findAll(booleanExpression)
                : batchRepository.findAll(booleanExpression, pageable).getContent();

        return batchMapper.getViewListFromEntityList(batchList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BatchView create(CreateBatchRequest createBatchRequest, Long updatedBy) {
        Society society = societyRepository.findById(createBatchRequest.getSocietyId()).orElse(null);
        if (society == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        Batch batch = new Batch();

        NullAwareBeanUtils.copyPropertiesWithoutNull(createBatchRequest, batch);
        batch.setSociety(society);
        batch.setUpdatedBy(updatedBy.toString());

        if (!CollectionUtils.isEmpty(createBatchRequest.getCreateBatchScheduleRequestList())) {
            batch.setBatchScheduleList(this.createBatchScheduleForNewBatch(createBatchRequest.getCreateBatchScheduleRequestList(), batch, updatedBy));

            for(BatchSchedule batchSchedule : batch.getBatchScheduleList()){
                batchScheduleDbService.checkConflictingSchedule(batchSchedule, batch);
            }
        }

        batchRepository.save(batch);

        try{
            CompletableFuture.runAsync(() -> sessionDbService.populateSessionsForBatch(batch), populateBatchSessionExecutor);
        } catch (Exception e) {
            log.error("Error while submitting task to populate sessions executor");
        }

        return batchMapper.getViewFromEntity(batch);
    }

    private List<BatchSchedule> createBatchScheduleForNewBatch(List<CreateBatchScheduleRequest> createBatchScheduleRequestList, Batch batch, Long updatedBy) {
        List<BatchSchedule> batchScheduleList = new ArrayList<>();

        for(CreateBatchScheduleRequest createBatchScheduleRequest : createBatchScheduleRequestList){
            BatchSchedule batchSchedule = new BatchSchedule();

            NullAwareBeanUtils.copyPropertiesWithoutNull(createBatchScheduleRequest, batchSchedule);
            batchSchedule.setBatch(batch);
            batchSchedule.setUpdatedBy(updatedBy.toString());

            batchScheduleList.add(batchSchedule);
        }

        return batchScheduleList;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BatchView update(UpdateBatchRequest updateBatchRequest, Long updatedBy) {
        Batch batch = batchRepository.getByIdAndIsDeleted(updateBatchRequest.getBatchId(), false);
        if(batch == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        if(updateBatchRequest.getSocietyId() != null) {
            Society society = societyRepository.findById(updateBatchRequest.getSocietyId()).orElse(null);
            if (society == null)
                throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

            batch.setSociety(society);
        }

        NullAwareBeanUtils.copyPropertiesWithoutNull(updateBatchRequest, batch);
        batch.setUpdatedBy(updatedBy.toString());

        batchRepository.save(batch);

        if(updateBatchRequest.getInstructorId() != null) {
            try {
                CompletableFuture.runAsync(() -> sessionDbService.updateInstructorId(updateBatchRequest.getBatchId(), updateBatchRequest.getInstructorId()), updateSessionInstructorExecutor);
            } catch (Exception e) {
                log.error("Error while submitting task to the update session instructor executor");
            }
        }

        return batchMapper.getViewFromEntity(batch);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markBatchAsDeleted(Long batchId, Boolean deleteTodaysSession, Long updatedBy) {
        batchRepository.markAsDeleted(batchId, updatedBy.toString());

        try {
            CompletableFuture.runAsync(() -> batchScheduleDbService.deleteBatchScheduleByBatch(batchId, deleteTodaysSession), deleteBatchScheduleExecutor);

        } catch (Exception e){
            log.error("Error in submitting task to delete batch schedule by batch executor");
        }
    }
}
