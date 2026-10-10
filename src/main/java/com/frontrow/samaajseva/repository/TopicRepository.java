package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.Topic;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends CrudRepository<Topic, Long>, QuerydslPredicateExecutor<Topic> {

    @NotNull
    @Override
    List<Topic> findAll(@NotNull Predicate predicate);

    @Query(value = "select c.category_id from topic t inner join chapter c on c.id = t.chapter_id where t.id = :topicId", nativeQuery = true)
    Long getTopicCategory(Long topicId);

    Topic getByIdAndIsDeleted(Long id, Boolean isDeleted);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update topic set is_deleted = true, updated_by = :updatedBy where id = :id", nativeQuery = true)
    void markAsDeleted(Long id, String updatedBy);
}
