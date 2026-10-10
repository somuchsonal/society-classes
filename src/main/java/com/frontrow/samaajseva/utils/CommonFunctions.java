package com.frontrow.samaajseva.utils;

import com.frontrow.samaajseva.enums.Level;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
public class CommonFunctions {

    public static Pageable getPageable(Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        Pageable pageable = null;
        pageNumber = pageNumber == null ? 0 : pageNumber-1;
        pageNumber = pageNumber < 0 ? 0: pageNumber;
        sortOrder = sortOrder == null ? Sort.Direction.ASC.name() : Sort.Direction.valueOf(sortOrder.toUpperCase()).name();

        if (pageSize != null && sortColumn == null) {
            pageable = PageRequest.of(pageNumber.intValue(), pageSize.intValue());
        }
        else if (pageSize == null && sortColumn != null) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
            pageable = PageRequest.of(0, Constants.DEFAULT_PAGE_SIZE, sort);
        }
        else if (pageSize != null) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
            pageable = PageRequest.of(pageNumber.intValue(), pageSize.intValue(), sort);
        }

        return pageable;
    }

    public static String getLevelFromLevels(Level level, Integer sublevel, Integer subSubLevel){
        return level.toString() + Constants.SPACE + sublevel + Constants.DOT + subSubLevel;
    }
}
