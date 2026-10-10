package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.Lead;
import com.frontrow.samaajseva.entity.Society;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.LeadMapper;
import com.frontrow.samaajseva.pojo.request.CreateLeadRequest;
import com.frontrow.samaajseva.pojo.view.LeadView;
import com.frontrow.samaajseva.repository.LeadRepository;
import com.frontrow.samaajseva.repository.SocietyRepository;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LeadDbService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;
    private final SocietyRepository societyRepository;

    @Autowired
    public LeadDbService(LeadRepository leadRepository,
                         LeadMapper leadMapper,
                         SocietyRepository societyRepository) {
        this.leadRepository = leadRepository;
        this.leadMapper = leadMapper;
        this.societyRepository = societyRepository;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public LeadView create(CreateLeadRequest createLeadRequest, Long updatedBy) {
        Society society = societyRepository.findById(createLeadRequest.getSocietyId()).orElse(null);
        if(society == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        Lead lead = new Lead();
        NullAwareBeanUtils.copyPropertiesWithoutNull(createLeadRequest, lead);
        lead.setSociety(society);
        lead.setUpdatedBy(updatedBy.toString());

        leadRepository.save(lead);

        return leadMapper.getViewFromEntity(lead);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<LeadView> createBulkLeads(List<CreateLeadRequest> createLeadRequests, Long updatedBy) {
        if(CollectionUtils.isEmpty(createLeadRequests))
            return new ArrayList<>();

        List<Society> societyList = societyRepository.getAllByIdIn(createLeadRequests.stream().map(CreateLeadRequest::getSocietyId).toList());
        Map<Long, Society> idSocietyMap = societyList.stream().collect(Collectors.toMap(Society::getId, Function.identity()));

        List<Lead> leads = new ArrayList<>();

        for(CreateLeadRequest createLeadRequest : createLeadRequests) {
            Lead lead = new Lead();
            NullAwareBeanUtils.copyPropertiesWithoutNull(createLeadRequest, lead);
            
            Society society = idSocietyMap.get(createLeadRequest.getSocietyId());
            if(society == null)
                continue;

            lead.setSociety(society);
            lead.setUpdatedBy(updatedBy.toString());

            leads.add(lead);
        }

        leadRepository.saveAll(leads);

        return leadMapper.getViewListFromEntityList(leads);
    }
}
