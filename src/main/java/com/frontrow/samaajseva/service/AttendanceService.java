package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.KidAttendanceDbService;
import com.frontrow.samaajseva.db.InstructorAttendanceDbService;
import com.frontrow.samaajseva.enums.Attendance;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.request.MarkInstructorAttendanceRequest;
import com.frontrow.samaajseva.pojo.response.KidAttendedResponseView;
import com.frontrow.samaajseva.pojo.view.KidAttendanceView;
import com.frontrow.samaajseva.pojo.view.InstructorAttendanceView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class AttendanceService {

    private final KidAttendanceDbService kidAttendanceDbService;
    private final InstructorAttendanceDbService instructorAttendanceDbService;

    @Autowired
    public AttendanceService(KidAttendanceDbService kidAttendanceDbService,
                             InstructorAttendanceDbService instructorAttendanceDbService) {
        this.kidAttendanceDbService = kidAttendanceDbService;
        this.instructorAttendanceDbService = instructorAttendanceDbService;
    }

    public List<KidAttendanceView> getKidAttendanceByFilter(List<Long> kidIds, List<Long> sessionIds, List<SessionType> sessionTypes, Attendance attendance, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return kidAttendanceDbService.getByFilter(kidIds, sessionIds, sessionTypes, attendance,  pageSize, pageNumber, sortColumn, sortOrder);
    }

    public List<KidAttendanceView> markKidsAttendance(List<Long> kidIds, Long sessionId, SessionType sessionType, Long updatedBy) {
        return kidAttendanceDbService.markKidsAttendance(kidIds, sessionId, sessionType, updatedBy);
    }

    public InstructorAttendanceView markInstructorAttendance(MarkInstructorAttendanceRequest markInstructorAttendanceRequest, Long updatedBy) {
        return instructorAttendanceDbService.markInstructorAttendance(markInstructorAttendanceRequest, updatedBy);
    }

    public void deleteKidsAttendance(List<Long> kidIds, Long sessionId, SessionType sessionType, Long updatedBy) {
        kidAttendanceDbService.markAbsent(kidIds, sessionId, sessionType, updatedBy);
    }

    public List<KidAttendedResponseView> getKidAttendanceBetweenDates(Long kidId, Long categoryId, LocalDate startDateAfter, LocalDate startDateBefore) {
        return kidAttendanceDbService.getKidAttendanceBetweenDates(kidId, categoryId, startDateAfter, startDateBefore);
    }

    public void populateAbsenteeism() {
        kidAttendanceDbService.populateAbsenteeism();
    }
}
