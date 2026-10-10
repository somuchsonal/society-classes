package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.KidTopicTracking;
import com.frontrow.samaajseva.pojo.response.CompletionPercentageResponse;
import com.frontrow.samaajseva.pojo.response.CurrentLevelResponse;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KidTopicTrackingRepository extends CrudRepository<KidTopicTracking, Long>, QuerydslPredicateExecutor<KidTopicTracking> {

    @NotNull
    @Override
    List<KidTopicTracking> findAll(@NotNull Predicate predicate);

    @Query(value = "select c.level as level, c.sub_level as subLevel, c.sub_sub_level as subSubLevel " +
            "from kid_topic_tracking ktt " +
            "inner join topic t on t.id = ktt.topic_id and t.is_deleted = false " +
            "inner join chapter c on c.id = t.chapter_id and c.is_deleted = false " +
            "where ktt.kid_id = :kidId and c.category_id = :categoryId ", nativeQuery = true)
    List<CurrentLevelResponse> getAllLevels(Long kidId, Long categoryId);

    KidTopicTracking getByKidIdAndTopicId(Long kidId, Long topicId);

    void deleteByKidIdAndTopicId(Long kidId, Long topicId);

    @Query(value = "select count(t.id) as totalTopicCount, c.`level` as level, count(ktt.id) as totalCompletionCount " +
                   "from topic t " +
                   "inner join chapter c on t.chapter_id = c.id " +
                   "left join kid_topic_tracking ktt on ktt.topic_id = t.id " +
                   "where c.category_id = :categoryId and (ktt.kid_id is null or ktt.kid_id = :kidId) " +
                   "and t.is_deleted = false and c.is_deleted = false " +
                   "group by c.`level` ", nativeQuery = true)
    List<CompletionPercentageResponse> getCompletionPercentageResponses(Long kidId, Long categoryId);
}
