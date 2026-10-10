package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Topic;
import com.frontrow.samaajseva.pojo.view.TopicView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class TopicMapper implements AbstractMapper<Topic, TopicView> {

    private final ChapterMapper chapterMapper;

    @Autowired
    public TopicMapper(ChapterMapper chapterMapper) {
        this.chapterMapper = chapterMapper;
    }

    @Override
    public TopicView getViewFromEntity(Topic topic) {
        TopicView topicView = new TopicView();

        if(topic != null){
            NullAwareBeanUtils.copyPropertiesWithoutNull(topic, topicView);
            topicView.setChapterView(chapterMapper.getViewFromEntity(topic.getChapter()));
        }

        return topicView;
    }

    @Override
    public List<TopicView> getViewListFromEntityList(List<Topic> topicList) {
        List<TopicView> topicViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(topicList)){
            for (Topic topic : topicList){
                topicViewList.add(this.getViewFromEntity(topic));
            }
        }

        return topicViewList;
    }
}
