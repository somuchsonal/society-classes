package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.Video;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends CrudRepository<Video, Long>, QuerydslPredicateExecutor<Video> {

    @NotNull
    @Override
    List<Video> findAll(@NotNull Predicate predicate);
}
