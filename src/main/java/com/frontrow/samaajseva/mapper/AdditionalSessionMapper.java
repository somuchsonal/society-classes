package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity. AdditionalSession;
import com.frontrow.samaajseva.pojo.view. AdditionalSessionView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdditionalSessionMapper implements AbstractMapper<AdditionalSession, AdditionalSessionView> {

    @Override
    public AdditionalSessionView getViewFromEntity(AdditionalSession additionalSession) {
        AdditionalSessionView additionalSessionView = new AdditionalSessionView();
        
        if(additionalSession != null)
            NullAwareBeanUtils.copyPropertiesWithoutNull(additionalSession, additionalSessionView);
        
        return additionalSessionView;
    }

    @Override
    public List<AdditionalSessionView> getViewListFromEntityList(List<AdditionalSession> additionalSessionList) {
        List<AdditionalSessionView> additionalSessionViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(additionalSessionList)){
            for(AdditionalSession additionalSession : additionalSessionList){
                additionalSessionViewList.add(this.getViewFromEntity(additionalSession));
            }
        }

        return additionalSessionViewList;
    }
}
