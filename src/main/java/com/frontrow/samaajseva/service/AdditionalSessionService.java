package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.AdditionalSessionDbService;
import com.frontrow.samaajseva.db.AdditionalSessionKidMappingDbService;
import com.frontrow.samaajseva.pojo.request.CreateAdditionalSessionRequest;
import com.frontrow.samaajseva.pojo.request.UpdateAdditionalSessionRequest;
import com.frontrow.samaajseva.pojo.view.AdditionalSessionView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class AdditionalSessionService {

    private final AdditionalSessionDbService additionalSessionDbService;
    private final AdditionalSessionKidMappingDbService additionalSessionKidMappingDbService;

    @Autowired
    public AdditionalSessionService(AdditionalSessionDbService additionalSessionDbService,
                                    AdditionalSessionKidMappingDbService additionalSessionKidMappingDbService) {
        this.additionalSessionDbService = additionalSessionDbService;
        this.additionalSessionKidMappingDbService = additionalSessionKidMappingDbService;
    }

    public List<AdditionalSessionView> getByFilter(List<Long> ids, List<Long> instructorIds, LocalDate startDateAfter, LocalDate startDateBefore, List<Long> categoryIds, Boolean isCancelled, List<Long> kidIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return additionalSessionDbService.getByFilter(ids, instructorIds, startDateAfter, startDateBefore, categoryIds, isCancelled, kidIds, pageSize, pageNumber, sortColumn, sortOrder);
    }

    public AdditionalSessionView create(CreateAdditionalSessionRequest createAdditionalSessionRequest, Long updatedBy) {
        return additionalSessionDbService.create(createAdditionalSessionRequest, updatedBy);
    }

    public AdditionalSessionView update(UpdateAdditionalSessionRequest updateAdditionalSessionRequest, Long updatedBy) {
        return additionalSessionDbService.update(updateAdditionalSessionRequest, updatedBy);
    }

    public void cancel(Long id, Long updatedBy) {
        additionalSessionDbService.markAsCancelled(id, updatedBy);
    }

    public void addKidToAdditionalSession(Long kidId, Long additionalSessionId, Long updatedBy) {
        additionalSessionKidMappingDbService.addKidToAdditionalSession(kidId, additionalSessionId, updatedBy);
    }

    public void removeKidFromAdditionalSession(Long kidId, Long additionalSessionId) {
        additionalSessionKidMappingDbService.removeKidFromAdditionalSession(kidId, additionalSessionId);
    }
}
