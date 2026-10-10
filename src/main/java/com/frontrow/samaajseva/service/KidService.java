package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.KidDbService;
import com.frontrow.samaajseva.pojo.request.CreateKidRequest;
import com.frontrow.samaajseva.pojo.view.KidView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class KidService {

    private final KidDbService kidDbService;

    @Autowired
    public KidService(KidDbService kidDbService) {
        this.kidDbService = kidDbService;
    }

    public List<KidView> getByFilter(List<Long> ids, List<Long> parentUserIds, List<Long> batchIds, Boolean hasActiveMembership, String searchKeyword, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return kidDbService.getByFilter(ids, parentUserIds, batchIds, hasActiveMembership, searchKeyword, pageSize, pageNumber, sortColumn, sortOrder);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public KidView create(CreateKidRequest createKidRequest, Long updatedBy) {
        return kidDbService.create(createKidRequest, updatedBy);
    }

    public List<KidView> getUnassignedKids() {
        return kidDbService.getUnassignedKids();
    }
}
