package com.frontrow.samaajseva.config;

import com.frontrow.samaajseva.service.AttendanceService;
import com.frontrow.samaajseva.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class Scheduler {

    private final SessionService sessionService;
    private final AttendanceService attendanceService;

    @Autowired
    public Scheduler(SessionService sessionService,
                     AttendanceService attendanceService) {
        this.sessionService = sessionService;
        this.attendanceService = attendanceService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void populateBatchSessions(){
        sessionService.populateBatchSessions();
    }

    @Scheduled(cron = "0 31 9 * * *")
    public void removeExtraSessions(){
        sessionService.removeExtraSessions();
    }

    @Scheduled(cron = "0 30 17 * * *")
    public void populateAbsenteeism() {
        attendanceService.populateAbsenteeism();
    }
}
