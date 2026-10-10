package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.SessionDbService;
import com.frontrow.samaajseva.enums.CancellationReason;
import com.frontrow.samaajseva.pojo.view.SessionView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class SessionService {

    private final SessionDbService sessionDbService;

    @Autowired
    public SessionService(SessionDbService sessionDbService) {
        this.sessionDbService = sessionDbService;
    }

    public List<SessionView> getByFilter(List<Long> ids, LocalDate startDateAfter, LocalDate startDateBefore, List<Long> batchIds, List<Long> instructorIds, List<String> venues, Boolean isCancelled, List<CancellationReason> cancellationReasons, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return sessionDbService.getByFilter(ids, startDateAfter, startDateBefore, batchIds, instructorIds, venues, isCancelled, cancellationReasons, pageSize, pageNumber, sortColumn, sortOrder);
    }

    public void populateBatchSessions() {
        sessionDbService.populateBatchSessions();
    }

    public void removeExtraSessions() {
        sessionDbService.removeExtraSessions();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancel(Long sessionId, LocalDate sessionDate, Long batchId, CancellationReason cancellationReason, String holidayName, Long updatedBy) {
        sessionDbService.markSessionAsCancelled(sessionId, sessionDate, batchId, cancellationReason, holidayName, updatedBy);

    }

    public void markDelay(Long sessionId, Integer minutesDelayed, Long updatedBy) {
        sessionDbService.markDelay(sessionId, minutesDelayed, updatedBy);
    }
}
