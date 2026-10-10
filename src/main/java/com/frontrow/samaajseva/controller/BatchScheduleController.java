package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.request.UpdateBatchScheduleRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.BatchView;
import com.frontrow.samaajseva.service.BatchScheduleService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/batch/schedule", consumes = "application/json", produces = "application/json")
@Tag(name = "Batch Schedule Controller")
public class BatchScheduleController extends AbstractController{

    private final BatchScheduleService batchScheduleService;

    @Autowired
    public BatchScheduleController(BatchScheduleService batchScheduleService) {
        this.batchScheduleService = batchScheduleService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Add a schedule for a batch")
    @Timed
    public Response<BatchView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                      @RequestBody @Valid CreateBatchScheduleRequest createBatchScheduleRequest){
        BatchView batchView = batchScheduleService.createBatchSchedule(createBatchScheduleRequest, updatedBy);
        return (Response<BatchView>) getSuccessResponse(batchView);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @Operation(summary = "Modify a schedule for a batch")
    @Timed
    public Response<BatchView> update(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                      @RequestBody @Valid UpdateBatchScheduleRequest updateBatchScheduleRequest){
        BatchView batchView = batchScheduleService.updateBatchSchedule(updateBatchScheduleRequest, updatedBy);
        return (Response<BatchView>) getSuccessResponse(batchView);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(summary = "Add a schedule for a batch")
    @Timed
    public Response<?> delete(@RequestParam(value = "batchScheduleId") Long batchScheduleId,
                              @RequestParam(value = "deleteTodaysSession", required = false, defaultValue = "false") Boolean deleteTodaysSession) {
        batchScheduleService.deleteBatchScheduleById(batchScheduleId, deleteTodaysSession);
        return getSuccessResponse(null);
    }
}
