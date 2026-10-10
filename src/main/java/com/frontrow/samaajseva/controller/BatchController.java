package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateBatchRequest;
import com.frontrow.samaajseva.pojo.request.UpdateBatchRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.BatchView;
import com.frontrow.samaajseva.service.BatchService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/batch", consumes = "application/json", produces = "application/json")
@Tag(name = "Batch Controller")
public class BatchController extends AbstractController {

    private final BatchService batchService;

    @Autowired
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get batch based on given filters")
    @Timed
    public Response<List<BatchView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                 @RequestParam(value = "societyIds", required = false) List<Long> societyIds,
                                                 @RequestParam(value = "instructorIds", required = false) List<Long> instructorIds,
                                                 @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                 @RequestParam(value = "clusterIds", required = false) List<Long> clusterIds,
                                                 @RequestParam(value = "cityIds", required = false) List<Long> cityIds,
                                                 @RequestParam(value = "kidIds", required = false) List<Long> kidIds,
                                                 @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
                                                 @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                 @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                 @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                 @RequestParam(value = "sortOrder", required = false) String sortOrder){
        List<BatchView> batchViewList = batchService.getByFilter(ids, societyIds, instructorIds, categoryIds, clusterIds, cityIds, kidIds, isDeleted, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<BatchView>>) getSuccessResponse(batchViewList);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Create a batch based on given details")
    @Timed
    public Response<BatchView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long userId,
                                      @RequestBody @Valid CreateBatchRequest createBatchRequest) {
        BatchView batchView = batchService.create(createBatchRequest, userId);
        return (Response<BatchView>) getSuccessResponse(batchView);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @Operation(summary = "Update a batch based on given details")
    @Timed
    public Response<BatchView> update(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                      @RequestBody @Valid UpdateBatchRequest updateBatchRequest) {
        BatchView batchView = batchService.update(updateBatchRequest, updatedBy);
        return (Response<BatchView>) getSuccessResponse(batchView);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(summary = "Delete a batch by given id")
    @Timed
    public Response<?> delete(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                              @RequestParam(value = "batchId") Long batchId,
                              @RequestParam(value = "deleteTodaysSession", required = false, defaultValue = "true") Boolean deleteTodaysSession) {
        batchService.delete(batchId, deleteTodaysSession, updatedBy);
        return getSuccessResponse(null);
    }
}
