package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateKidRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.KidView;
import com.frontrow.samaajseva.service.KidService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/kid", consumes = "application/json", produces = "application/json")
@Tag(name = "Kids Controller")
public class KidController extends AbstractController {

    private final KidService kidService;

    @Autowired
    public KidController(KidService kidService) {
        this.kidService = kidService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get kids based on given filters")
    @Timed
    public Response<List<KidView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                               @RequestParam(value = "parentUserIds", required = false) List<Long> parentUserIds,
                                               @RequestParam(value = "batchIds", required = false) List<Long> batchIds,
                                               @RequestParam(value = "hasActiveMembership", required = false) Boolean hasActiveMembership,
                                               @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                               @RequestParam(value = "pageSize", required = false) Long pageSize,
                                               @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                               @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                               @RequestParam(value = "sortOrder", required = false) String sortOrder){
        List<KidView> kidViewList = kidService.getByFilter(ids, parentUserIds, batchIds, hasActiveMembership, searchKeyword, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<KidView>>) getSuccessResponse(kidViewList);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "API to create a kid")
    @Timed
    public Response<KidView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long userId,
                                    @RequestBody @Valid CreateKidRequest createKidRequest) {
        KidView kidView = kidService.create(createKidRequest, userId);
        return (Response<KidView>) getSuccessResponse(kidView);
    }

    @RequestMapping(value = "/unassigned", method = RequestMethod.GET)
    @Operation(summary = "To get kids with no assigned batch")
    @Timed
    public Response<List<KidView>> getUnassignedKids() {
        List<KidView> kidViewList = kidService.getUnassignedKids();
        return (Response<List<KidView>>) getSuccessResponse(kidViewList);
    }

}
