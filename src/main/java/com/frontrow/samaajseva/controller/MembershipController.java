package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.MembershipView;
import com.frontrow.samaajseva.service.MembershipService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/membership", consumes = "application/json", produces = "application/json")
@Tag(name = "Membership Controller")
public class MembershipController extends AbstractController {
    
    private final MembershipService membershipService;

    @Autowired
    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get membership by filter")
    @Timed
    public Response<List<MembershipView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                      @RequestParam(value = "kidIds", required = false) List<Long> kidIds,
                                                      @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                      @RequestParam(value = "societyIds", required = false) List<Long> societyIds,
                                                      @RequestParam(value = "validityInMonths", required = false) Integer validityInMonths,
                                                      @RequestParam(value = "orderIds", required = false) List<Long> orderIds,
                                                      @RequestParam(value = "isPartlyPaid", required = false) Boolean isPartlyPaid,
                                                      @RequestParam(value = "startDateAfter", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDateAfter,
                                                      @RequestParam(value = "startDateBefore", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDateBefore,
                                                      @RequestParam(value = "endDateAfter", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDateAfter,
                                                      @RequestParam(value = "endDateBefore", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDateBefore,
                                                      @RequestParam(value = "hasNotStarted", required = false) Boolean hasNotStarted,
                                                      @RequestParam(value = "parentUserIds", required = false) List<Long> parentUserIds,
                                                      @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                      @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                      @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                      @RequestParam(value = "sortOrder", required = false) String sortOrder){
        
        List<MembershipView> membershipViewList = membershipService.getByFilter(ids, kidIds, categoryIds, societyIds, validityInMonths, orderIds, isPartlyPaid, startDateAfter, startDateBefore, endDateAfter, endDateBefore, hasNotStarted, parentUserIds, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<MembershipView>>) getSuccessResponse(membershipViewList);
    }
}
