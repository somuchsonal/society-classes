package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Lead;
import com.frontrow.samaajseva.pojo.view.LeadView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeadMapper implements AbstractMapper<Lead, LeadView> {

    private final SocietyMapper societyMapper;

    @Autowired
    public LeadMapper(SocietyMapper societyMapper) {
        this.societyMapper = societyMapper;
    }


    @Override
    public LeadView getViewFromEntity(Lead lead) {
        LeadView leadView = new LeadView();

        if(lead != null) {
            NullAwareBeanUtils.copyPropertiesWithoutNull(lead, leadView);
            leadView.setSocietyView(societyMapper.getViewFromEntity(lead.getSociety()));
        }

        return leadView;
    }

    @Override
    public List<LeadView> getViewListFromEntityList(List<Lead> leadList) {
        List<LeadView> leadViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(leadList)){
            for (Lead lead: leadList) {
                leadViewList.add(this.getViewFromEntity(lead));
            }
        }

        return leadViewList;
    }
}
