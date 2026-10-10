package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.City;
import com.frontrow.samaajseva.pojo.view.CityView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class CityMapper implements AbstractMapper<City, CityView> {

    @Override
    public CityView getViewFromEntity(City city) {
        CityView cityView = new CityView();

        if(city != null)
            NullAwareBeanUtils.copyPropertiesWithoutNull(city, cityView);

        return cityView;
    }

    public List<CityView> getViewListFromEntityList(List<City> cityList) {
        List<CityView> cityViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(cityList)){
            for (City city: cityList) {
                cityViewList.add(this.getViewFromEntity(city));
            }
        }

        return cityViewList;
    }
}

