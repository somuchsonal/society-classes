package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.InstructorAttendance;
import com.frontrow.samaajseva.pojo.view.InstructorAttendanceView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class InstructorAttendanceMapper implements AbstractMapper<InstructorAttendance, InstructorAttendanceView> {

    @Override
    public InstructorAttendanceView getViewFromEntity(InstructorAttendance instructorAttendance) {
        InstructorAttendanceView instructorAttendanceView = new InstructorAttendanceView();

        if(instructorAttendance != null)
            NullAwareBeanUtils.copyPropertiesWithoutNull(instructorAttendance, instructorAttendanceView);

        return instructorAttendanceView;
    }

    @Override
    public List<InstructorAttendanceView> getViewListFromEntityList(List<InstructorAttendance> instructorAttendanceList) {
        List<InstructorAttendanceView> instructorAttendanceViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(instructorAttendanceList)){
            for(InstructorAttendance instructorAttendance : instructorAttendanceList){
                instructorAttendanceViewList.add(this.getViewFromEntity(instructorAttendance));
            }
        }

        return instructorAttendanceViewList;
    }
}
