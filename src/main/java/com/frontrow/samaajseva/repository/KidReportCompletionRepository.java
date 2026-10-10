package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.KidReportCompletion;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KidReportCompletionRepository extends CrudRepository<KidReportCompletion, Long>, QuerydslPredicateExecutor<KidReportCompletion> {

    KidReportCompletion getByKidIdAndChapterId(Long kidId, Long chapterId);

    @NotNull
    @Override
    List<KidReportCompletion> findAll(@NotNull Predicate predicate);
}
