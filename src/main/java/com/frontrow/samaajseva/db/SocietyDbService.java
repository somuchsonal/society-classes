package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.Cluster;
import com.frontrow.samaajseva.entity.QCluster;
import com.frontrow.samaajseva.entity.Society;
import com.frontrow.samaajseva.entity.QSociety;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.SocietyMapper;
import com.frontrow.samaajseva.pojo.request.CreateSocietyRequest;
import com.frontrow.samaajseva.pojo.view.SocietyView;
import com.frontrow.samaajseva.repository.ClusterRepository;
import com.frontrow.samaajseva.repository.SocietyRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
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
public class SocietyDbService {

    private final SocietyRepository societyRepository;
    private final SocietyMapper societyMapper;
    private final ClusterRepository clusterRepository;

    @Autowired
    public SocietyDbService(SocietyRepository societyRepository,
                            SocietyMapper societyMapper,
                            ClusterRepository clusterRepository) {
        this.societyRepository = societyRepository;
        this.societyMapper = societyMapper;
        this.clusterRepository = clusterRepository;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<SocietyView> getByFilter(List<Long> ids, List<String> names, List<Long> clusterIds, List<Long> cityIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        QSociety qSociety = QSociety.society;
        QCluster qCluster = QCluster.cluster;

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qSociety.id.in(ids));

        if(!CollectionUtils.isEmpty(names))
            booleanExpression = booleanExpression.and(qSociety.name.in(names));

        if(!CollectionUtils.isEmpty(clusterIds))
            booleanExpression = booleanExpression.and(qSociety.clusterId.in(clusterIds));

        if(!CollectionUtils.isEmpty(cityIds))
            booleanExpression = booleanExpression.and(qSociety.clusterId.in(JPAExpressions.select(qCluster.id)
                                                                                          .from(qCluster)
                                                                                          .where(qCluster.cityId.in(cityIds))));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Society> societyList =  pageable == null ?
                societyRepository.findAll(booleanExpression)
                : societyRepository.findAll(booleanExpression, pageable).getContent();

        return societyMapper.getViewListFromEntityList(societyList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SocietyView create(CreateSocietyRequest createSocietyRequest, Long updatedBy) {
        Cluster cluster = clusterRepository.findById(createSocietyRequest.getClusterId()).orElse(null);
        if(cluster == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        Society society = new Society();

        NullAwareBeanUtils.copyPropertiesWithoutNull(createSocietyRequest, society);
        society.setCluster(cluster);
        society.setUpdatedBy(updatedBy.toString());

        societyRepository.save(society);

        return societyMapper.getViewFromEntity(society);
    }
}
