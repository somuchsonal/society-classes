package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Society;
import com.frontrow.samaajseva.pojo.view.SocietyView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class SocietyMapper implements AbstractMapper<Society, SocietyView> {

    private final ClusterMapper clusterMapper;

    @Autowired
    public SocietyMapper(ClusterMapper clusterMapper) {
        this.clusterMapper = clusterMapper;
    }

    @Override
    public SocietyView getViewFromEntity(Society society) {
        SocietyView societyView = new SocietyView();

        if(society != null) {
            NullAwareBeanUtils.copyPropertiesWithoutNull(society, societyView);
            societyView.setClusterView(clusterMapper.getViewFromEntity(society.getCluster()));
        }

        return societyView;
    }

    @Override
    public List<SocietyView> getViewListFromEntityList(List<Society> societyList) {
        List<SocietyView> societyViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(societyList)){
            for (Society society: societyList) {
                societyViewList.add(this.getViewFromEntity(society));
            }
        }

        return societyViewList;
    }
}
