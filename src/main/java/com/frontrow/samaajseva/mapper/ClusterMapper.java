package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Cluster;
import com.frontrow.samaajseva.pojo.view.ClusterView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClusterMapper implements AbstractMapper<Cluster, ClusterView> {

    private final CityMapper cityMapper;

    @Autowired
    public ClusterMapper(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    @Override
    public ClusterView getViewFromEntity(Cluster cluster) {
        ClusterView clusterView = new ClusterView();

        if(cluster != null){
            NullAwareBeanUtils.copyPropertiesWithoutNull(cluster, clusterView);
            clusterView.setCityView(cityMapper.getViewFromEntity(cluster.getCity()));
        }

        return clusterView;
    }

    @Override
    public List<ClusterView> getViewListFromEntityList(List<Cluster> clusterList) {
        List<ClusterView> clusterViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(clusterList)){
            for (Cluster cluster: clusterList) {
                clusterViewList.add(this.getViewFromEntity(cluster));
            }
        }

        return clusterViewList;
    }
}
