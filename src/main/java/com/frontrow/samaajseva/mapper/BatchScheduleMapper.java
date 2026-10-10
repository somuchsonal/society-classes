package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.BatchSchedule;
import com.frontrow.samaajseva.pojo.view.BatchScheduleView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class BatchScheduleMapper implements AbstractMapper<BatchSchedule, BatchScheduleView> {

    @Override
    public BatchScheduleView getViewFromEntity(BatchSchedule batchSchedule) {
        BatchScheduleView batchScheduleView = new BatchScheduleView();

        if(batchSchedule != null)
            NullAwareBeanUtils.copyPropertiesWithoutNull(batchSchedule, batchScheduleView);

        return batchScheduleView;
    }

    @Override
    public List<BatchScheduleView> getViewListFromEntityList(List<BatchSchedule> batchScheduleList) {
        List<BatchScheduleView> batchScheduleViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(batchScheduleList)){
            for (BatchSchedule batchSchedule : batchScheduleList) {
                batchScheduleViewList.add(this.getViewFromEntity(batchSchedule));
            }
        }

        return batchScheduleViewList;
    }
    
}
