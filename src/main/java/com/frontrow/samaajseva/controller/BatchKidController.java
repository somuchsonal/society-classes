package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.service.BatchService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/batch/kid", consumes = "application/json", produces = "application/json")
@Tag(name = "Batch Kid Mapping Controller")
public class BatchKidController extends AbstractController {

    private final BatchService batchService;

    @Autowired
    public BatchKidController(BatchService batchService) {
        this.batchService = batchService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Add kids to a batch")
    @Timed
    public Response<?> addKidToBatch(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                     @RequestParam(value = "kidId") Long kidId,
                                     @RequestParam(value = "batchId") Long batchId) {
        batchService.addKidToBatch(kidId, batchId, updatedBy);
        return getSuccessResponse(null);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(summary = "Remove kid from a batch")
    @Timed
    public Response<?> removeKidFromBatch(@RequestParam(value = "kidId") Long kidId,
                                          @RequestParam(value = "batchId") Long batchId) {
        batchService.removeKidFromBatch(kidId, batchId);
        return getSuccessResponse(null);
    }
}
