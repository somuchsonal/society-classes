package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.KidAttendance;
import com.frontrow.samaajseva.pojo.view.KidAttendanceView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KidAttendanceMapper implements AbstractMapper<KidAttendance, KidAttendanceView> {

    private final KidMapper kidMapper;
    private final SessionMapper sessionMapper;

    public KidAttendanceMapper(KidMapper kidMapper,
                               SessionMapper sessionMapper) {
        this.kidMapper = kidMapper;
        this.sessionMapper = sessionMapper;
    }


    @Override
    public KidAttendanceView getViewFromEntity(KidAttendance kidAttendance) {
        KidAttendanceView kidAttendanceView = new KidAttendanceView();

        if(kidAttendance != null){
            NullAwareBeanUtils.copyPropertiesWithoutNull(kidAttendance, kidAttendanceView);
        }

        return kidAttendanceView;
    }

    @Override
    public List<KidAttendanceView> getViewListFromEntityList(List<KidAttendance> kidAttendanceList) {
        List<KidAttendanceView> kidAttendanceViewList = new ArrayList<>();

        for(KidAttendance kidAttendance : kidAttendanceList){
            kidAttendanceViewList.add(this.getViewFromEntity(kidAttendance));
        }

        return kidAttendanceViewList;
    }
}
