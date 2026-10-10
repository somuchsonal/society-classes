package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.config.properties.MiscProperties;
import com.frontrow.samaajseva.entity.*;
import com.frontrow.samaajseva.mapper.KidMapper;
import com.frontrow.samaajseva.pojo.request.CreateKidRequest;
import com.frontrow.samaajseva.pojo.view.KidView;
import com.frontrow.samaajseva.repository.KidRepository;
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

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class KidDbService {

    private final KidRepository kidRepository;
    private final KidMapper kidMapper;
    private final MiscProperties miscProperties;

    @Autowired
    public KidDbService(KidRepository kidRepository,
                        KidMapper kidMapper,
                        MiscProperties miscProperties) {
        this.kidRepository = kidRepository;
        this.kidMapper = kidMapper;
        this.miscProperties = miscProperties;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidView> getByFilter(List<Long> ids, List<Long> parentUserIds, List<Long> batchIds, Boolean hasActiveMembership, String searchKeyword, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        QKid qKid = QKid.kid;
        QBatchKidMapping qBatchKidMapping = QBatchKidMapping.batchKidMapping;
        QMembership qMembership = QMembership.membership;

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qKid.id.in(ids));

        if(!CollectionUtils.isEmpty(parentUserIds))
            booleanExpression = booleanExpression.and(qKid.parentUserId.in(parentUserIds));

        if(searchKeyword != null)
            booleanExpression = booleanExpression.and(qKid.firstName.likeIgnoreCase(searchKeyword).or(qKid.lastName.likeIgnoreCase(searchKeyword)));

        if(!CollectionUtils.isEmpty(batchIds))
            booleanExpression = booleanExpression.and(qKid.id.in(JPAExpressions.select(qBatchKidMapping.kidId)
                                                                               .from(qBatchKidMapping)
                                                                               .where(qBatchKidMapping.batchId.in(batchIds))));

        //Active membership definition has some buffer days in case of partial payment for class list
        if(hasActiveMembership != null && hasActiveMembership)
            booleanExpression = booleanExpression.and(qKid.id.in(JPAExpressions.select(qMembership.kidId)
                                                                               .from(qMembership)
                                                                               .where(qMembership.endDate.isNull()
                                                                                       .or(qMembership.endDate.after(LocalDate.now()))
                                                                                       .or(qMembership.endDate.eq(LocalDate.now()))
                                                                                       .or(qMembership.isPartlyPaid.isTrue()
                                                                                               .and(qMembership.endDate.after(LocalDate.now().minusDays(miscProperties.getBufferDaysCount())))))));

        if(hasActiveMembership != null && !hasActiveMembership)
            booleanExpression = booleanExpression.and(qKid.id.in(JPAExpressions.select(qMembership.kidId)
                                                                               .from(qMembership)
                                                                               .where(qMembership.endDate.before(LocalDate.now().minusDays(miscProperties.getBufferDaysCount()))
                                                                                       .or(qMembership.isPartlyPaid.isFalse()
                                                                                               .and(qMembership.endDate.before(LocalDate.now()))))));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Kid> kidList =  pageable == null ?
                kidRepository.findAll(booleanExpression)
                : kidRepository.findAll(booleanExpression, pageable).getContent();

        return kidMapper.getViewListFromEntityList(kidList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public KidView create(CreateKidRequest createKidRequest, Long updatedBy) {

        Kid kid = new Kid();

        NullAwareBeanUtils.copyPropertiesWithoutNull(createKidRequest, kid);
        kid.setUpdatedBy(updatedBy.toString());

        kid = kidRepository.save(kid);

        return kidMapper.getViewFromEntity(kid);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidView> getActiveKidParentIdsFromBatch(Long batchId){
        LocalDate currentDateWithBuffer = LocalDate.now().minusDays(miscProperties.getBufferDaysCount());
        return kidMapper.getViewListFromEntityList(kidRepository.getActiveKidsFromBatch(batchId, LocalDate.now(), currentDateWithBuffer));
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<KidView> getUnassignedKids() {
        List<Kid> kidList = kidRepository.getUnassignedKids();

        return kidMapper.getViewListFromEntityList(kidList);
    }
}
