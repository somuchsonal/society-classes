package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateLeadRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.LeadView;
import com.frontrow.samaajseva.service.LeadService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/lead", consumes = "application/json", produces = "application/json")
@Tag(name = "Lead Controller")
public class LeadController extends AbstractController {

    private final LeadService leadService;

    @Autowired
    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Create leads based on given input")
    @Timed
    public Response<LeadView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                     @RequestBody @Valid CreateLeadRequest createLeadRequest) {
        LeadView leadView = leadService.create(createLeadRequest, updatedBy);

        return (Response<LeadView>) getSuccessResponse(leadView);
    }

    @RequestMapping(value = "/bulk", method = RequestMethod.POST)
    @Operation(summary = "Create bulk leads based on given input")
    @Timed
    public Response<List<LeadView>> createBulkLeads(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                              @RequestBody @Valid List<CreateLeadRequest> createLeadRequests) {
        List<LeadView> leadViews = leadService.createBulkLeads(createLeadRequests, updatedBy);

        return (Response<List<LeadView>>) getSuccessResponse(leadViews);
    }

}
