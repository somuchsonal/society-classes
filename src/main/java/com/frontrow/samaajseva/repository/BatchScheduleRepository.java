package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.BatchSchedule;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BatchScheduleRepository extends CrudRepository<BatchSchedule, Long>, QuerydslPredicateExecutor<BatchSchedule> {

    void deleteById(@NotNull Long id);

    BatchSchedule getById(Long batchScheduleId);

    @Query(value =  "select bs.id from batch b inner join batch_schedule bs on bs.batchId = b.id " +
                    "where b.societyId = :societyId and b.isDeleted = false " +
                    "and bs.dayOfWeek = :dayOfWeek and bs.venue = :venue " +
                    "and ((bs.startTime > :startTime and bs.startTime < :endTime) " +
                    "or (bs.endTime > :startTime and bs.endTime < :endTime) " +
                    "or (bs.startTime <= :startTime and bs.endTime >= :endTime))")
    List<Long> checkVenueConflicts(String venue, Long societyId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);

    @Query(value =  "select bs.id from batch b inner join batch_schedule bs on bs.batchId = b.id " +
                    "where b.instructorId = :instructorId and bs.dayOfWeek = :dayOfWeek and b.isDeleted = false " +
                    "and ((bs.startTime > :startTime and bs.startTime < :endTime) " +
                    "or (bs.endTime > :startTime and bs.endTime < :endTime) " +
                    "or (bs.startTime <= :startTime and bs.endTime >= :endTime))")
    List<Long> checkInstructorConflicts(Long instructorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);

    void deleteByBatchId(Long batchId);
}
