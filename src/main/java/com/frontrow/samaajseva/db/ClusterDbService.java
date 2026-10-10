package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.City;
import com.frontrow.samaajseva.entity.Cluster;
import com.frontrow.samaajseva.entity.QCluster;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.ClusterMapper;
import com.frontrow.samaajseva.pojo.request.CreateClusterRequest;
import com.frontrow.samaajseva.pojo.view.ClusterView;
import com.frontrow.samaajseva.repository.CityRepository;
import com.frontrow.samaajseva.repository.ClusterRepository;
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
public class ClusterDbService {

    private final ClusterRepository clusterRepository;
    private final ClusterMapper clusterMapper;
    private final CityRepository cityRepository;

    @Autowired
    public ClusterDbService(ClusterRepository clusterRepository,
                            ClusterMapper clusterMapper,
                            CityDbService cityDbService, CityRepository cityRepository) {
        this.clusterRepository = clusterRepository;
        this.clusterMapper = clusterMapper;
        this.cityRepository = cityRepository;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<ClusterView> getByFilter(List<Long> ids, List<String> names, List<Long> cityIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        QCluster qCluster = QCluster.cluster;

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qCluster.id.in(ids));

        if(!CollectionUtils.isEmpty(names))
            booleanExpression = booleanExpression.and(qCluster.name.in(names));

        if(!CollectionUtils.isEmpty(cityIds))
            booleanExpression = booleanExpression.and(qCluster.cityId.in(cityIds));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Cluster> clusterList =  pageable == null ?
                clusterRepository.findAll(booleanExpression)
                : clusterRepository.findAll(booleanExpression, pageable).getContent();

        return clusterMapper.getViewListFromEntityList(clusterList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ClusterView create(CreateClusterRequest createClusterRequest, Long updatedBy) {
        City city = cityRepository.findById(createClusterRequest.getCityId()).orElse(null);
        if(city == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        Cluster cluster = new Cluster();

        NullAwareBeanUtils.copyPropertiesWithoutNull(createClusterRequest, cluster);
        cluster.setCity(city);
        cluster.setUpdatedBy(updatedBy.toString());

        clusterRepository.save(cluster);

        return clusterMapper.getViewFromEntity(cluster);
    }
}
