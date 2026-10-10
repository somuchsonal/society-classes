package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Kid;
import com.frontrow.samaajseva.pojo.view.KidView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class KidMapper implements AbstractMapper<Kid, KidView> {

    @Override
    public KidView getViewFromEntity(Kid kid) {
        KidView kidView = new KidView();

        if(kid != null)
            NullAwareBeanUtils.copyPropertiesWithoutNull(kid, kidView);

        return kidView;
    }

    @Override
    public List<KidView> getViewListFromEntityList(List<Kid> kidList) {
        List<KidView> kidViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(kidList)){
            for (Kid kid: kidList) {
                kidViewList.add(this.getViewFromEntity(kid));
            }
        }

        return kidViewList;
    }
}
