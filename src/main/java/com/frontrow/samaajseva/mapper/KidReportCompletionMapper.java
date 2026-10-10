package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.KidReportCompletion;
import com.frontrow.samaajseva.pojo.view.KidReportCompletionView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class KidReportCompletionMapper implements AbstractMapper<KidReportCompletion, KidReportCompletionView> {

    private final KidMapper kidMapper;
    private final ChapterMapper chapterMapper;

    @Autowired
    public KidReportCompletionMapper(KidMapper kidMapper,
                                     ChapterMapper chapterMapper) {
        this.kidMapper = kidMapper;
        this.chapterMapper = chapterMapper;
    }


    @Override
    public KidReportCompletionView getViewFromEntity(KidReportCompletion kidReportCompletion) {
        KidReportCompletionView kidReportCompletionView = new KidReportCompletionView();

        if(kidReportCompletion != null) {
            NullAwareBeanUtils.copyPropertiesWithoutNull(kidReportCompletion, kidReportCompletionView);
            kidReportCompletionView.setKidView(kidMapper.getViewFromEntity(kidReportCompletion.getKid()));
            kidReportCompletionView.setChapterView(chapterMapper.getViewFromEntity(kidReportCompletion.getChapter()));
        }

        return kidReportCompletionView;
    }

    @Override
    public List<KidReportCompletionView> getViewListFromEntityList(List<KidReportCompletion> kidReportCompletionList) {
        List<KidReportCompletionView> kidReportCompletionViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(kidReportCompletionList)) {
            for(KidReportCompletion kidReportCompletion : kidReportCompletionList) {
                kidReportCompletionViewList.add(this.getViewFromEntity(kidReportCompletion));
            }
        }

        return kidReportCompletionViewList;
    }
}
