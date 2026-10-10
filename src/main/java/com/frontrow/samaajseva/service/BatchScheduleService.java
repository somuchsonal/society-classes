package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.BatchScheduleDbService;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.pojo.request.CreateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.request.UpdateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.view.BatchView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BatchScheduleService {
    
    private final BatchScheduleDbService batchScheduleDbService;

    @Autowired
    public BatchScheduleService(BatchScheduleDbService batchScheduleDbService) {
        this.batchScheduleDbService = batchScheduleDbService;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BatchView createBatchSchedule(CreateBatchScheduleRequest createBatchScheduleRequest, Long updatedBy) {
        if(createBatchScheduleRequest.getBatchId() == null)
            throw new CustomException(ErrorCodes.BATCH_ID_NOT_PROVIDED);

        return batchScheduleDbService.createBatchSchedule(createBatchScheduleRequest, updatedBy);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BatchView updateBatchSchedule(UpdateBatchScheduleRequest updateBatchScheduleRequest, Long updatedBy) {
        return batchScheduleDbService.updateBatchSchedule(updateBatchScheduleRequest, updatedBy);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteBatchScheduleById(Long batchScheduleId, Boolean deleteTodaysSession) {
        batchScheduleDbService.deleteBatchScheduleById(batchScheduleId, deleteTodaysSession);
    }
}
