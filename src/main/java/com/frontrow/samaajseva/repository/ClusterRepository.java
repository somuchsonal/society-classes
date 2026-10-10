package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.Cluster;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClusterRepository extends CrudRepository<Cluster, Long>, QuerydslPredicateExecutor<Cluster> {

    @NotNull
    @Override
    List<Cluster> findAll(@NotNull Predicate predicate);
}
