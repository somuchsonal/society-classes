package com.frontrow.samaajseva.comparator;

import com.frontrow.samaajseva.pojo.response.CurrentLevelResponse;

import java.util.Comparator;

public class CurrentLevelResponseComparator implements Comparator<CurrentLevelResponse> {
    @Override
    public int compare(CurrentLevelResponse o1, CurrentLevelResponse o2) {
        if(o1.getLevel().getOrder().equals(o2.getLevel().getOrder())){
            if (o1.getSubLevel().equals(o2.getSubLevel())){
                return -1 * o1.getSubSubLevel().compareTo(o2.getSubSubLevel());

            } else {
                return -1 * o1.getSubLevel().compareTo(o2.getSubLevel());
            }

        } else {
            return -1 * o1.getLevel().getOrder().compareTo(o2.getLevel().getOrder());
        }
    }
}
