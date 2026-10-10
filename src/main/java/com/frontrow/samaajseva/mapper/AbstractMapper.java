package com.frontrow.samaajseva.mapper;

import java.util.List;

public interface AbstractMapper<E, V> {
    V getViewFromEntity(E e);

    List<V> getViewListFromEntityList(List<E> eList);
}
