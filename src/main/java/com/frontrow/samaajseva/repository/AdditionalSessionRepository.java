package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.AdditionalSession;
import com.frontrow.samaajseva.pojo.response.InstructorSessionConflictResponse;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

@Repository
public interface AdditionalSessionRepository extends CrudRepository<AdditionalSession, Long>, QuerydslPredicateExecutor<AdditionalSession> {

    @NotNull
    @Override
    List<AdditionalSession> findAll(@NotNull Predicate predicate);

    AdditionalSession getByIdAndIsCancelled(Long id, Boolean isCancelled);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update additional_session set is_cancelled = true, updated_by = :updatedBy where id = :id", nativeQuery = true)
    void markAsCancelled(Long id, String updatedBy);

    @Query(value = "select s.id as sessionId, 'BATCH_SESSION' as sessionType " +
                   "from `session` s " +
                   "where s.instructor_id = :instructorId and s.start_date = :date and s.is_cancelled = false " +
                   "and ((s.start_time > :startTime and s.start_time < :endTime) " +
                   "or (s.end_time > :startTime and s.end_time < :endTime) " +
                   "or (s.start_time <= :startTime and s.end_time >= :endTime)) " +
                   "union " +
                   "select ads.id as sessionId, 'ADDITIONAL_SESSION' as sessionType " +
                   "from additional_session ads " +
                   "where ads.instructor_id = :instructorId and ads.start_date = :date and ads.is_cancelled = false " +
                   "and ((ads.start_time > :startTime and ads.start_time < :endTime) " +
                   "or (ads.end_time > :startTime and ads.end_time < :endTime) " +
                   "or (ads.start_time <= :startTime and ads.end_time >= :endTime))", nativeQuery = true)
    List<InstructorSessionConflictResponse> getConflictingSessions(Long instructorId, LocalDate date, LocalTime startTime, LocalTime endTime);
}
