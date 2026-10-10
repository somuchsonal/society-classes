package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.enums.CancellationReason;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.SessionView;
import com.frontrow.samaajseva.service.SessionService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/session", consumes = "application/json", produces = "application/json")
@Tag(name = "Session Controller")
public class SessionController extends AbstractController {

    private final SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get session based on given filters")
    @Timed
    public Response<List<SessionView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                   @RequestParam(value = "startDateAfter", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDateAfter,
                                                   @RequestParam(value = "startDateBefore", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDateBefore,
                                                   @RequestParam(value = "batchIds", required = false) List<Long> batchIds,
                                                   @RequestParam(value = "instructorIds", required = false) List<Long> instructorIds,
                                                   @RequestParam(value = "venues", required = false) List<String> venues,
                                                   @RequestParam(value = "isCancelled", required = false) Boolean isCancelled,
                                                   @RequestParam(value = "cancellationReasons", required = false) List<CancellationReason> cancellationReasons,
                                                   @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                   @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                   @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                   @RequestParam(value = "sortOrder", required = false) String sortOrder){

        List<SessionView> sessionViewList = sessionService.getByFilter(ids, startDateAfter, startDateBefore, batchIds, instructorIds, venues, isCancelled, cancellationReasons, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<SessionView>>) getSuccessResponse(sessionViewList);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(summary = "Cancel a particular batch session")
    @Timed
    public Response<?> cancel(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                              @RequestParam(value = "sessionId") Long sessionId,
                              @RequestParam(value = "sessionDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate sessionDate,
                              @RequestParam(value = "batchId") Long batchId,
                              @RequestParam(value = "cancellationReason") CancellationReason cancellationReason,
                              @RequestParam(value = "holidayName", required = false) String holidayName){
        sessionService.cancel(sessionId, sessionDate, batchId, cancellationReason, holidayName, updatedBy);
        return getSuccessResponse(null);
    }

    @RequestMapping(value = "/markDelay", method = RequestMethod.POST)
    @Operation(summary = "Mark delay in start of session")
    @Timed
    public Response<?> markDelay(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                 @RequestParam(value = "sessionId") Long sessionId,
                                 @RequestParam(value = "minutesDelayed") Integer minutesDelayed){
        sessionService.markDelay(sessionId, minutesDelayed, updatedBy);
        return getSuccessResponse(null);
    }
}
