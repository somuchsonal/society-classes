package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Chapter;
import com.frontrow.samaajseva.pojo.view.ChapterView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChapterMapper implements AbstractMapper<Chapter, ChapterView> {

    @Override
    public ChapterView getViewFromEntity(Chapter chapter) {
        ChapterView chapterView = new ChapterView();

        if(chapter != null)
            NullAwareBeanUtils.copyPropertiesWithoutNull(chapter, chapterView);

        return chapterView;
    }

    @Override
    public List<ChapterView> getViewListFromEntityList(List<Chapter> chapterList) {
        List<ChapterView> chapterViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(chapterList)){
            for(Chapter chapter : chapterList){
                chapterViewList.add(this.getViewFromEntity(chapter));
            }
        }

        return chapterViewList;
    }
}
