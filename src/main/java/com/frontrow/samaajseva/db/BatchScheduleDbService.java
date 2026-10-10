package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.Batch;
import com.frontrow.samaajseva.entity.BatchSchedule;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.BatchMapper;
import com.frontrow.samaajseva.mapper.BatchScheduleMapper;
import com.frontrow.samaajseva.pojo.request.CreateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.request.UpdateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.view.BatchScheduleView;
import com.frontrow.samaajseva.pojo.view.BatchView;
import com.frontrow.samaajseva.repository.BatchRepository;
import com.frontrow.samaajseva.repository.BatchScheduleRepository;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BatchScheduleDbService {

    private final BatchScheduleRepository batchScheduleRepository;
    private final BatchScheduleMapper batchScheduleMapper;
    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final SessionDbService sessionDbService;
    private final ThreadPoolTaskExecutor updateSessionScheduleExecutor;
    private final ThreadPoolTaskExecutor deleteSessionFromScheduleExecutor;
    private final ThreadPoolTaskExecutor populateBatchSessionExecutor;

    @Autowired
    public BatchScheduleDbService(BatchScheduleRepository batchScheduleRepository,
                                  BatchScheduleMapper batchScheduleMapper,
                                  BatchRepository batchRepository,
                                  BatchMapper batchMapper,
                                  SessionDbService sessionDbService,
                                  @Qualifier("updateSessionScheduleExecutor") ThreadPoolTaskExecutor updateSessionScheduleExecutor,
                                  @Qualifier("deleteSessionFromScheduleExecutor") ThreadPoolTaskExecutor deleteSessionFromScheduleExecutor,
                                  @Qualifier("populateBatchSessionExecutor") ThreadPoolTaskExecutor populateBatchSessionExecutor) {
        this.batchScheduleRepository = batchScheduleRepository;
        this.batchScheduleMapper = batchScheduleMapper;
        this.batchRepository = batchRepository;
        this.batchMapper = batchMapper;
        this.sessionDbService = sessionDbService;
        this.updateSessionScheduleExecutor = updateSessionScheduleExecutor;
        this.deleteSessionFromScheduleExecutor = deleteSessionFromScheduleExecutor;
        this.populateBatchSessionExecutor = populateBatchSessionExecutor;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BatchView createBatchSchedule(CreateBatchScheduleRequest createBatchScheduleRequest, Long updatedBy) {
        Batch batch = batchRepository.getByIdAndIsDeleted(createBatchScheduleRequest.getBatchId(), false);
        if(batch == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        BatchSchedule batchSchedule = new BatchSchedule();
        NullAwareBeanUtils.copyPropertiesWithoutNull(createBatchScheduleRequest, batchSchedule);
        batchSchedule.setBatch(batch);
        batchSchedule.setUpdatedBy(updatedBy.toString());

        batch.getBatchScheduleList().add(batchSchedule);

        this.checkConflictingSchedule(batchSchedule, batch);

        batchRepository.save(batch);

        try{
            CompletableFuture.runAsync(() -> sessionDbService.populateSessionsForBatch(batch), populateBatchSessionExecutor);

        } catch (Exception e){
            log.error("Error in submitting task to populate batch schedule session executor");
        }

        return batchMapper.getViewFromEntity(batch);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BatchView updateBatchSchedule(UpdateBatchScheduleRequest updateBatchScheduleRequest, Long updatedBy) {
        Batch batch = batchRepository.getByIdAndIsDeleted(updateBatchScheduleRequest.getBatchId(), false);
        if(batch == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        BatchScheduleView oldBatchScheduleView = null;
        BatchSchedule updatedBatchSchedule = null;

        for(BatchSchedule batchSchedule : batch.getBatchScheduleList()){
            if(batchSchedule.getId().equals(updateBatchScheduleRequest.getBatchScheduleId())){
                oldBatchScheduleView = batchScheduleMapper.getViewFromEntity(batchSchedule);

                NullAwareBeanUtils.copyPropertiesWithoutNull(updateBatchScheduleRequest, batchSchedule);
                batchSchedule.setUpdatedBy(updatedBy.toString());
                updatedBatchSchedule = batchSchedule;

                if(!batchSchedule.getStartTime().isBefore(batchSchedule.getEndTime()))
                    throw new CustomException(ErrorCodes.START_TIME_SHOULD_BE_BEFORE_END_TIME);
            }
        }

        if(oldBatchScheduleView == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        this.checkConflictingSchedule(updatedBatchSchedule, batch);

        batchRepository.save(batch);

        try{
            BatchScheduleView finalOldBatchScheduleView = oldBatchScheduleView;
            CompletableFuture.runAsync(() -> sessionDbService.updateSessionSchedule(updateBatchScheduleRequest, finalOldBatchScheduleView), updateSessionScheduleExecutor);
        } catch (Exception e) {
            log.error("Error while submitting task to update session schedule executor");
        }

        return batchMapper.getViewFromEntity(batch);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteBatchScheduleById(Long batchScheduleId, Boolean deleteTodaysSession) {
        BatchSchedule batchSchedule = batchScheduleRepository.getById(batchScheduleId);
        batchScheduleRepository.delete(batchSchedule);

        try {
            CompletableFuture.runAsync(() -> sessionDbService.deleteSessionsFromSchedule(batchSchedule, deleteTodaysSession), deleteSessionFromScheduleExecutor);
        } catch (Exception e) {
            log.error("Error in submitting task to delete session from schedule executor");
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteBatchScheduleByBatch(Long batchId, Boolean deleteTodaysSession) {
        batchScheduleRepository.deleteByBatchId(batchId);

        try {
            CompletableFuture.runAsync(() -> sessionDbService.deleteSessionsByBatch(batchId, deleteTodaysSession), deleteSessionFromScheduleExecutor);
        } catch (Exception e) {
            log.error("Error in submitting task to delete session from batch executor");
        }
    }

    public void checkConflictingSchedule(BatchSchedule batchSchedule, Batch batch) {
        if(!CollectionUtils.isEmpty(batch.getBatchScheduleList()) && batch.getBatchScheduleList().size() > 1) {
            for(BatchSchedule compareBatchSchedule : batch.getBatchScheduleList()){
                if(this.areConflictingBatchSchedules(batchSchedule, compareBatchSchedule))
                            throw new CustomException(ErrorCodes.BATCH_CONFLICTS_IN_SCHEDULE);
            }
        }

        List<Long> conflictingIds = batchScheduleRepository.checkVenueConflicts(batchSchedule.getVenue(), batch.getSocietyId(), batchSchedule.getDayOfWeek(), batchSchedule.getStartTime(), batchSchedule.getEndTime());

        // Checking if size of ids list is greater than 1; or if the 1 id returned, it is not same the schedule id we are checking for conflicts
        if(!CollectionUtils.isEmpty(conflictingIds) && (conflictingIds.size() > 1 || !conflictingIds.get(0).equals(batchSchedule.getId())))
            throw new CustomException(ErrorCodes.VENUE_CONFLICTS_IN_SCHEDULE);

        conflictingIds = batchScheduleRepository.checkInstructorConflicts(batch.getInstructorId(), batchSchedule.getDayOfWeek(), batchSchedule.getStartTime(), batchSchedule.getEndTime());

        if(!CollectionUtils.isEmpty(conflictingIds) && (conflictingIds.size() > 1 || !conflictingIds.get(0).equals(batchSchedule.getId())))
            throw new CustomException(ErrorCodes.INSTRUCTOR_CONFLICTS_IN_SCHEDULE);
    }

    private boolean areConflictingBatchSchedules(BatchSchedule batchSchedule, BatchSchedule compareBatchSchedule) {
        // Checking if the schedules have same day of week; if they are not the same object; if they have overlapping times;
        // if all true, checking if both ids are null or if they have different ids
        return compareBatchSchedule.getDayOfWeek().equals(batchSchedule.getDayOfWeek())
                && !Objects.equals(batchSchedule, compareBatchSchedule)
                && this.hasOverlappingTimes(batchSchedule, compareBatchSchedule)
                && ((batchSchedule.getId() == null && compareBatchSchedule.getId() == null)
                    || !Objects.equals(batchSchedule.getId(), compareBatchSchedule.getId()));
    }

    private boolean hasOverlappingTimes(BatchSchedule batchSchedule, BatchSchedule compareBatchSchedule) {
        // Checking if the schedule1 completely covers/extends the other schedule, or if the startTime1/endTime1 is falling between the other schedule
        return (compareBatchSchedule.getStartTime().compareTo(batchSchedule.getStartTime()) <= 0 && compareBatchSchedule.getEndTime().compareTo(batchSchedule.getEndTime()) >= 0)
                || (compareBatchSchedule.getStartTime().isAfter(batchSchedule.getStartTime()) && compareBatchSchedule.getStartTime().isBefore(batchSchedule.getEndTime()))
                || (compareBatchSchedule.getEndTime().isAfter(batchSchedule.getStartTime()) && compareBatchSchedule.getEndTime().isBefore(batchSchedule.getEndTime()));
    }
}
