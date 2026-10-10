package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Batch;
import com.frontrow.samaajseva.pojo.view.BatchView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class BatchMapper implements AbstractMapper<Batch, BatchView> {

    private final SocietyMapper societyMapper;
    private final BatchScheduleMapper batchScheduleMapper;

    @Autowired
    public BatchMapper(SocietyMapper societyMapper,
                       BatchScheduleMapper batchScheduleMapper) {
        this.societyMapper = societyMapper;
        this.batchScheduleMapper = batchScheduleMapper;
    }

    @Override
    public BatchView getViewFromEntity(Batch batch) {
        BatchView batchView = new BatchView();

        if(batch != null) {
            NullAwareBeanUtils.copyPropertiesWithoutNull(batch, batchView);
            batchView.setSocietyView(societyMapper.getViewFromEntity(batch.getSociety()));

            batchView.setBatchScheduleViewList(batchScheduleMapper.getViewListFromEntityList(batch.getBatchScheduleList()));
        }

        return batchView;
    }

    @Override
    public List<BatchView> getViewListFromEntityList(List<Batch> batchList) {
        List<BatchView> batchViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(batchList)){
            for (Batch batch: batchList) {
                batchViewList.add(this.getViewFromEntity(batch));
            }
        }

        return batchViewList;
    }
}
