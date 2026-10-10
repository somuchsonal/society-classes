package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.AdditionalSessionKidMapping;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdditionalSessionKidMappingRepository extends CrudRepository<AdditionalSessionKidMapping, Long>, QuerydslPredicateExecutor<AdditionalSessionKidMapping> {

    @NotNull
    @Override
    List<AdditionalSessionKidMapping> findAll(@NotNull Predicate predicate);

    void deleteByKidIdAndAdditionalSessionId(Long kidId, Long additionalSessionId);
}
