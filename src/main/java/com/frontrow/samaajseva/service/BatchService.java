package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.BatchDbService;
import com.frontrow.samaajseva.db.BatchKidMappingDbService;
import com.frontrow.samaajseva.db.KidDbService;
import com.frontrow.samaajseva.db.MembershipDbService;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.pojo.request.CreateBatchRequest;
import com.frontrow.samaajseva.pojo.request.CreateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.request.UpdateBatchRequest;
import com.frontrow.samaajseva.pojo.view.BatchView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BatchService {

    private final BatchDbService batchDbService;
    private final KidDbService kidDbService;
    private final MembershipDbService membershipDbService;
    private final BatchKidMappingDbService batchKidMappingDbService;

    @Autowired
    public BatchService(BatchDbService batchDbService,
                        KidDbService kidDbService,
                        MembershipDbService membershipDbService,
                        BatchKidMappingDbService batchKidMappingDbService) {
        this.batchDbService = batchDbService;
        this.kidDbService = kidDbService;
        this.membershipDbService = membershipDbService;
        this.batchKidMappingDbService = batchKidMappingDbService;
    }

    public List<BatchView> getByFilter(List<Long> ids, List<Long> societyIds, List<Long> instructorIds, List<Long> categoryIds, List<Long> clusterIds, List<Long> cityIds, List<Long> kidIds, Boolean isDeleted, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return batchDbService.getByFilter(ids, societyIds, instructorIds, categoryIds, clusterIds, cityIds, kidIds, isDeleted, pageSize, pageNumber, sortColumn, sortOrder);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BatchView create(CreateBatchRequest createBatchRequest, Long updatedBy) {
        //Checking if create schedule request is attached to create batch request. If so, batch id should be empty in create schedule requests
        if(!CollectionUtils.isEmpty(createBatchRequest.getCreateBatchScheduleRequestList())
                && !CollectionUtils.isEmpty(createBatchRequest.getCreateBatchScheduleRequestList().stream().map(CreateBatchScheduleRequest::getBatchId).filter(Objects::nonNull).collect(Collectors.toList())))
            throw new CustomException(ErrorCodes.BATCH_ID_SHOULD_BE_EMPTY);

        return batchDbService.create(createBatchRequest, updatedBy);
    }

    public BatchView update(UpdateBatchRequest updateBatchRequest, Long updatedBy) {
        return batchDbService.update(updateBatchRequest, updatedBy);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(Long batchId, Boolean deleteTodaysSession, Long updatedBy) {
        batchDbService.markBatchAsDeleted(batchId, deleteTodaysSession, updatedBy);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addKidToBatch(Long kidId, Long batchId, Long updatedBy) {
        batchKidMappingDbService.addKidToBatch(kidId, batchId, updatedBy);
    }

    public void removeKidFromBatch(Long kidId, Long batchId) {
        batchKidMappingDbService.removeKidFromBatch(kidId, batchId);
    }
}
