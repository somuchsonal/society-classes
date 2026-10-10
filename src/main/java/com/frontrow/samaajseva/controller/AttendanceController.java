package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.enums.Attendance;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.request.MarkInstructorAttendanceRequest;
import com.frontrow.samaajseva.pojo.response.KidAttendedResponseView;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.InstructorAttendanceView;
import com.frontrow.samaajseva.pojo.view.KidAttendanceView;
import com.frontrow.samaajseva.service.AttendanceService;
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
@RequestMapping(value = "/attendance", consumes = "application/json", produces = "application/json")
@Tag(name = "Attendance Controller")
public class AttendanceController extends AbstractController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @RequestMapping(value = "/kid", method = RequestMethod.GET)
    @Operation(summary = "Get attendance by filter")
    @Timed
    Response<List<KidAttendanceView>> getByFilter(@RequestParam(value = "kidIds", required = false) List<Long> kidIds,
                                                  @RequestParam(value = "sessionIds", required = false) List<Long> sessionIds,
                                                  @RequestParam(value = "sessionTypes", required = false) List<SessionType> sessionTypes,
                                                  @RequestParam(value = "attendance", required = false) Attendance attendance,
                                                  @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                  @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                  @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                  @RequestParam(value = "sortOrder", required = false) String sortOrder){
        List<KidAttendanceView> kidAttendanceViewList = attendanceService.getKidAttendanceByFilter(kidIds, sessionIds, sessionTypes, attendance, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<KidAttendanceView>>) getSuccessResponse(kidAttendanceViewList);
    }

    @RequestMapping(value = "/kid", method = RequestMethod.POST)
    @Operation(summary = "Mark attendance for kids in a given session")
    @Timed
    Response<List<KidAttendanceView>> markKidsAttendance(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                                         @RequestParam(value = "kidIds") List<Long> kidIds,
                                                         @RequestParam(value = "sessionId") Long sessionId,
                                                         @RequestParam(value = "sessionType")SessionType sessionType){
        List<KidAttendanceView> kidAttendanceViewList = attendanceService.markKidsAttendance(kidIds, sessionId, sessionType, updatedBy);
        return (Response<List<KidAttendanceView>>) getSuccessResponse(kidAttendanceViewList);
    }

    @RequestMapping(value = "/kid", method = RequestMethod.DELETE)
    @Operation(summary = "Delete attendance for kids in a given session")
    @Timed
    Response<?> deleteKidsAttendance(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                     @RequestParam(value = "kidIds") List<Long> kidIds,
                                     @RequestParam(value = "sessionId") Long sessionId,
                                     @RequestParam(value = "sessionType") SessionType sessionType){
        attendanceService.deleteKidsAttendance(kidIds, sessionId, sessionType, updatedBy);
        return getSuccessResponse(null);
    }

    @RequestMapping(value = "/instructor", method = RequestMethod.POST)
    @Operation(summary = "Mark attendance for a given instructor for a session")
    @Timed
    Response<InstructorAttendanceView> markInstructorAttendance(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                                                @RequestBody @Valid MarkInstructorAttendanceRequest markInstructorAttendanceRequest){
        InstructorAttendanceView instructorAttendanceView = attendanceService.markInstructorAttendance(markInstructorAttendanceRequest, updatedBy);
        return (Response<InstructorAttendanceView>) getSuccessResponse(instructorAttendanceView);
    }

    @RequestMapping(value = "/kid/{kidId}/getBetweenDates", method = RequestMethod.GET)
    @Operation(summary = "Get kids attendance between dates for batch sessions")
    @Timed
    Response<List<KidAttendedResponseView>> getKidAttendanceBetweenDates(@PathVariable(value = "kidId") Long kidId,
                                                                         @RequestParam(value = "categoryId") Long categoryId,
                                                                         @RequestParam(value = "startDateAfter") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDateAfter,
                                                                         @RequestParam(value = "startDateBefore") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDateBefore){

        List<KidAttendedResponseView> kidAttendedResponseViews = attendanceService.getKidAttendanceBetweenDates(kidId, categoryId, startDateAfter, startDateBefore);
        return (Response<List<KidAttendedResponseView>>) getSuccessResponse(kidAttendedResponseViews);
    }

}
