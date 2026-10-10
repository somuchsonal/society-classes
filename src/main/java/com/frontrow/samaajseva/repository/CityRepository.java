package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.City;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends CrudRepository<City, Long>, QuerydslPredicateExecutor<City> {

    @NotNull
    @Override
    List<City> findAll(@NotNull Predicate predicate);

}
