package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.Chapter;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends CrudRepository<Chapter, Long>, QuerydslPredicateExecutor<Chapter> {

    @NotNull
    @Override
    List<Chapter> findAll(@NotNull Predicate predicate);

    Chapter getByIdAndIsDeleted(Long id, Boolean isDeleted);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update chapter set is_deleted = true, updated_by = :updatedBy where id = :id", nativeQuery = true)
    void markAsDeleted(Long id, String updatedBy);
}
