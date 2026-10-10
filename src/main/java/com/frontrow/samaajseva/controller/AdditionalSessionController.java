package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateAdditionalSessionRequest;
import com.frontrow.samaajseva.pojo.request.UpdateAdditionalSessionRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.AdditionalSessionView;
import com.frontrow.samaajseva.service.AdditionalSessionService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/additionalSession", consumes = "application/json", produces = "application/json")
@Tag(name = "Additional Sessions Controller")
public class AdditionalSessionController extends AbstractController {

    private final AdditionalSessionService additionalSessionService;

    @Autowired
    public AdditionalSessionController(AdditionalSessionService additionalSessionService) {
        this.additionalSessionService = additionalSessionService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get Additional Sessions based on given filters")
    @Timed
    public Response<List<AdditionalSessionView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                             @RequestParam(value = "instructorIds", required = false) List<Long> instructorIds,
                                                             @RequestParam(value = "startDateAfter", required = false)  @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDateAfter,
                                                             @RequestParam(value = "startDateBefore", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDateBefore,
                                                             @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                             @RequestParam(value = "isCancelled", required = false) Boolean isCancelled,
                                                             @RequestParam(value = "kidIds", required = false) List<Long> kidIds,
                                                             @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                             @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                             @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                             @RequestParam(value = "sortOrder", required = false) String sortOrder){

        List<AdditionalSessionView> additionalSessionViewList = additionalSessionService.getByFilter(ids, instructorIds, startDateAfter, startDateBefore, categoryIds, isCancelled, kidIds, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<AdditionalSessionView>>) getSuccessResponse(additionalSessionViewList);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Create additional session")
    @Timed
    public Response<AdditionalSessionView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                                  @RequestBody @Valid CreateAdditionalSessionRequest createAdditionalSessionRequest){
        AdditionalSessionView additionalSessionView = additionalSessionService.create(createAdditionalSessionRequest, updatedBy);
        return (Response<AdditionalSessionView>) getSuccessResponse(additionalSessionView);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @Operation(summary = "Update additional session based on Id")
    @Timed
    public Response<AdditionalSessionView> update(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                                  @RequestBody @Valid UpdateAdditionalSessionRequest updateAdditionalSessionRequest){
        AdditionalSessionView additionalSessionView = additionalSessionService.update(updateAdditionalSessionRequest, updatedBy);
        return (Response<AdditionalSessionView>) getSuccessResponse(additionalSessionView);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(summary = "Delete additional session based on id")
    @Timed
    public Response<?> cancel(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                              @RequestParam(value = "id") Long id){
        additionalSessionService.cancel(id, updatedBy);
        return getSuccessResponse(null);
    }
}
