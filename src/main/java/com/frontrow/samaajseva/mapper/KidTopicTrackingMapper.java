package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.KidTopicTracking;
import com.frontrow.samaajseva.pojo.view.KidTopicTrackingView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class KidTopicTrackingMapper implements AbstractMapper<KidTopicTracking, KidTopicTrackingView> {

    private final KidMapper kidMapper;
    private final TopicMapper topicMapper;

    @Autowired
    public KidTopicTrackingMapper(KidMapper kidMapper,
                                  TopicMapper topicMapper) {
        this.kidMapper = kidMapper;
        this.topicMapper = topicMapper;
    }

    @Override
    public KidTopicTrackingView getViewFromEntity(KidTopicTracking kidTopicTracking) {
        KidTopicTrackingView kidTopicTrackingView = new KidTopicTrackingView();

        if(kidTopicTracking != null){
            NullAwareBeanUtils.copyPropertiesWithoutNull(kidTopicTracking, kidTopicTrackingView);
            kidTopicTrackingView.setKidView(kidMapper.getViewFromEntity(kidTopicTracking.getKid()));
            kidTopicTrackingView.setTopicView(topicMapper.getViewFromEntity(kidTopicTracking.getTopic()));
        }

        return kidTopicTrackingView;
    }

    @Override
    public List<KidTopicTrackingView> getViewListFromEntityList(List<KidTopicTracking> kidTopicTrackingList) {
        List<KidTopicTrackingView> kidTopicTrackingViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(kidTopicTrackingList)){
            for(KidTopicTracking kidTopicTracking : kidTopicTrackingList){
                kidTopicTrackingViewList.add(this.getViewFromEntity(kidTopicTracking));
            }
        }

        return kidTopicTrackingViewList;
    }
}
