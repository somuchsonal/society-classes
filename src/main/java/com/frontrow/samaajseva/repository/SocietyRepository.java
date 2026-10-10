package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.Society;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocietyRepository extends CrudRepository<Society, Long>, QuerydslPredicateExecutor<Society> {

    @NotNull
    @Override
    List<Society> findAll(@NotNull Predicate predicate);

    List<Society> getAllByIdIn(List<Long> ids);
}
