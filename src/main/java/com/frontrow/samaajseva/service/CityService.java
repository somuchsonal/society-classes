package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.CityDbService;
import com.frontrow.samaajseva.pojo.request.CreateCityRequest;
import com.frontrow.samaajseva.pojo.view.CityView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CityService {

    private final CityDbService cityDbService;

    @Autowired
    public CityService(CityDbService cityDbService) {
        this.cityDbService = cityDbService;
    }

    public List<CityView> getByFilter(List<Long> ids, List<String> names, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return cityDbService.getByFilter(ids, names, pageSize, pageNumber, sortColumn, sortOrder);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public CityView create(CreateCityRequest createCityRequest, Long updatedBy) {
        return cityDbService.create(createCityRequest, updatedBy);
    }
}
