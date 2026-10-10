package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.City;
import com.frontrow.samaajseva.entity.QCity;
import com.frontrow.samaajseva.mapper.CityMapper;
import com.frontrow.samaajseva.pojo.request.CreateCityRequest;
import com.frontrow.samaajseva.pojo.view.CityView;
import com.frontrow.samaajseva.repository.CityRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class CityDbService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Autowired
    public CityDbService(CityRepository cityRepository,
                         CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<CityView> getByFilter(List<Long> ids, List<String> names, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        QCity qCity = QCity.city;

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qCity.id.in(ids));

        if(!CollectionUtils.isEmpty(names))
            booleanExpression = booleanExpression.and(qCity.name.in(names));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<City> cityList =  pageable == null ?
                cityRepository.findAll(booleanExpression)
                : cityRepository.findAll(booleanExpression, pageable).getContent();

        return cityMapper.getViewListFromEntityList(cityList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public CityView create(CreateCityRequest createCityRequest, Long updatedBy) {
        City city = new City();

        NullAwareBeanUtils.copyPropertiesWithoutNull(createCityRequest, city);
        city.setUpdatedBy(updatedBy.toString());

        cityRepository.save(city);

        return cityMapper.getViewFromEntity(city);
    }
}
