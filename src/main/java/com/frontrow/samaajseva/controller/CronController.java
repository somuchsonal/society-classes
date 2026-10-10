package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.service.AttendanceService;
import com.frontrow.samaajseva.service.SessionService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cron", consumes = "application/json", produces = "application/json")
@Tag(name = "Cron Controller")
public class CronController extends AbstractController {

    private final SessionService sessionService;
    private final AttendanceService attendanceService;

    @Autowired
    public CronController(SessionService sessionService,
                          AttendanceService attendanceService) {
        this.sessionService = sessionService;
        this.attendanceService = attendanceService;
    }

    @RequestMapping(value = "/populateSessions", method = RequestMethod.POST)
    @Operation(summary = "Populate sessions according to batch data")
    @Timed
    Response<?> populateBatchSessions(){
        sessionService.populateBatchSessions();
        return getSuccessResponse(null);
    }

    @RequestMapping(value = "/removeSessions", method = RequestMethod.DELETE)
    @Operation(summary = "Remove unnecessary sessions according to batch data")
    @Timed
    Response<?> removeExtraSessions(){
        sessionService.removeExtraSessions();
        return getSuccessResponse(null);
    }

    @RequestMapping(value = "/populateAbsenteeism", method = RequestMethod.POST)
    @Operation(summary = "Populate absent kids data in kid session attendance")
    @Timed
    Response<?> populateAbsenteeism(){
        attendanceService.populateAbsenteeism();
        return getSuccessResponse(null);
    }
}
