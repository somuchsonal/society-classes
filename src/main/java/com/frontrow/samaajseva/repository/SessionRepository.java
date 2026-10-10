package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.Session;
import com.frontrow.samaajseva.pojo.response.PopulateBatchSessionResponse;
import com.frontrow.samaajseva.pojo.response.SessionResponse;
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
public interface SessionRepository extends CrudRepository<Session, Long>, QuerydslPredicateExecutor<Session> {

    @NotNull
    @Override
    List<Session> findAll(@NotNull Predicate predicate);

    //todo s.is_cancelled = false will give wrong populated till, if the last session of the batch is cancelled
    @Query(value = "select b.id as batchId, bs.day_of_week as dayOfWeek, bs.start_time as startTime, bs.end_time as endTime, " +
                   "bs.venue as venue, bs.created_at as scheduleCreatedAt, max(s.start_date) as populatedTill,  " +
                   "b.instructor_id as instructorId, max(m.end_date) membershipsValidTill, " +
                   "min(bkm.created_at) as kidsAddedAt, max(m.validity_in_months) as membershipsValidForMonths " +
                   "from batch b " +
                   "inner join batch_schedule bs on b.id = bs.batch_id " +
                   "left join `session` s on b.id = s.batch_id and s.is_cancelled = false " +
                   "left join batch_kid_mapping bkm on bkm.batch_id = b.id " +
                   "left join membership m on bkm.kid_id = m.kid_id and (m.end_date > now() or m.end_date is null) " +
                   "where b.is_deleted is false and m.id is not null " +
                   "group by b.id, bs.id", nativeQuery = true)
    List<PopulateBatchSessionResponse> getBatchesToBePopulated();

    @Query(value = "select b.id as batchId, bs.day_of_week as dayOfWeek, bs.start_time as startTime, bs.end_time as endTime, " +
                   "bs.venue as venue, bs.created_at as scheduleCreatedAt, max(s.start_date) as populatedTill,  " +
                   "b.instructor_id as instructorId, max(m.end_date) membershipsValidTill, " +
                   "min(bkm.created_at) as kidsAddedAt, max(m.validity_in_months) as membershipsValidForMonths " +
                   "from batch b " +
                   "inner join batch_schedule bs on b.id = bs.batch_id " +
                   "left join `session` s on b.id = s.batch_id and s.is_cancelled = false " +
                   "left join batch_kid_mapping bkm on bkm.batch_id = b.id " +
                   "left join membership m on bkm.kid_id = m.kid_id and (m.end_date > now() or m.end_date is null) " +
                   "where b.is_deleted is false " +
                   "group by b.id, bs.id", nativeQuery = true)
    List<PopulateBatchSessionResponse> getBatchesToBeCleaned();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update session s set s.instructor_id = :instructorId " +
                   "where s.batch_id = :batchId and s.start_date > now() and s.is_cancelled is false", nativeQuery = true)
    void updateSessionInstructor(Long batchId, Long instructorId);

    List<Session> getByBatchIdAndStartTimeAndEndTimeAndStartDateAfter(Long batchId, LocalTime startTime, LocalTime endTime, LocalDate dateAfter);

    List<Session> getByBatchIdAndStartTimeAndEndTimeAndIsCancelledAndStartDateAfter(Long batchId, LocalTime startTime, LocalTime endTime, Boolean isCancelled, LocalDate dateAfter);

    List<Session> getByBatchIdAndIsCancelledAndStartDateAfter(Long batchId, Boolean isCancelled, LocalDate getFrom);

    void deleteByBatchIdAndIsCancelledAndStartDateAfter(Long batchId, Boolean isCancelled, LocalDate deleteFrom);

    Session getByIdAndIsCancelled(Long id, Boolean isCancelled);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update `session` set is_cancelled = true, cancellation_reason = :reason, updated_by = :updatedBy " +
                   "where id = :id and start_date = :sessionDate and batch_id = :batchId and is_cancelled = false", nativeQuery = true)
    int markAsCancelled(Long id, LocalDate sessionDate, Long batchId, String reason, String updatedBy);

    @Query(value =  "select s.start_date as startDate, b.id as batchId, b.category_id as categoryId from `session` s " +
                    "inner join batch b on s.batch_id = b.id", nativeQuery = true)
    SessionResponse getResponseById(Long sessionId);
}
