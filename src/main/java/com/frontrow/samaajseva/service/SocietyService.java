package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.SocietyDbService;
import com.frontrow.samaajseva.pojo.request.CreateSocietyRequest;
import com.frontrow.samaajseva.pojo.view.SocietyView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SocietyService {

    private final SocietyDbService societyDbService;

    @Autowired
    public SocietyService(SocietyDbService societyDbService) {
        this.societyDbService = societyDbService;
    }

    public List<SocietyView> getByFilter(List<Long> ids, List<String> names, List<Long> clusterIds, List<Long> cityIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return societyDbService.getByFilter(ids, names, clusterIds, cityIds, pageSize, pageNumber, sortColumn, sortOrder);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SocietyView create(CreateSocietyRequest createSocietyRequest, Long updatedBy) {
        return societyDbService.create(createSocietyRequest, updatedBy);
    }
}
