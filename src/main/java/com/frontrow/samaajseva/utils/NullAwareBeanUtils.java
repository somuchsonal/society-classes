package com.frontrow.samaajseva.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;

public class NullAwareBeanUtils {

    private static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyPropertiesWithoutNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static void copyPropertiesWithoutNull(Object src, Object target, String... ignoreProperties) {
        String[] emptyProperties = getNullPropertyNames(src);
        List<String> ignorePropertiesList = new ArrayList<>(Arrays.asList(emptyProperties));
        ignorePropertiesList.addAll(Arrays.asList(ignoreProperties));
        BeanUtils.copyProperties(src, target, ignorePropertiesList.toArray(new String[0]));
    }

    public static void copyProperties(Object src, Object target, String... ignoreProperties) {
        BeanUtils.copyProperties(src, target, ignoreProperties);
    }
}
