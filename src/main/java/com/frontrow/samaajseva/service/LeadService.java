package com.frontrow.samaajseva.service;


import com.frontrow.samaajseva.db.LeadDbService;
import com.frontrow.samaajseva.pojo.request.CreateLeadRequest;
import com.frontrow.samaajseva.pojo.view.LeadView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LeadService {

    private final LeadDbService leadDbService;

    @Autowired
    public LeadService(LeadDbService leadDbService) {
        this.leadDbService = leadDbService;
    }

    public LeadView create(CreateLeadRequest createLeadRequest, Long updatedBy) {
        return leadDbService.create(createLeadRequest, updatedBy);
    }

    public List<LeadView> createBulkLeads(List<CreateLeadRequest> createLeadRequests, Long updatedBy) {
        return leadDbService.createBulkLeads(createLeadRequests, updatedBy);
    }
}
