package com.frontrow.samaajseva.comparator;

import com.frontrow.samaajseva.entity.Chapter;

import java.util.Comparator;

public class ChapterComparator implements Comparator<Chapter> {

    @Override
    public int compare(Chapter o1, Chapter o2) {
        if(o1.getLevel().getOrder().equals(o2.getLevel().getOrder())){
            if (o1.getSubLevel().equals(o2.getSubLevel())){
                if(o1.getSubSubLevel().equals(o2.getSubSubLevel())){
                    return o1.getSortOrder().compareTo(o2.getSortOrder());

                } else {
                    return o1.getSubSubLevel().compareTo(o2.getSubSubLevel());
                }

            } else {
                return o1.getSubLevel().compareTo(o2.getSubLevel());
            }

        } else {
            return o1.getLevel().getOrder().compareTo(o2.getLevel().getOrder());
        }
    }
}
