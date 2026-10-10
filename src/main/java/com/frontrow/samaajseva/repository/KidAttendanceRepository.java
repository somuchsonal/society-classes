package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.KidAttendance;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.response.KidAttendedResponse;
import com.frontrow.samaajseva.pojo.response.SessionResponse;
import com.frontrow.samaajseva.pojo.response.SessionsMissedCountResponse;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KidAttendanceRepository extends CrudRepository<KidAttendance, Long>, QuerydslPredicateExecutor<KidAttendance> {

    @NotNull
    @Override
    List<KidAttendance> findAll(@NotNull Predicate predicate);

    @Query(value =  "select count(ka.id) as missedCount, max(m.start_date) as membershipStartDate " +
                    "from `session` s " +
                    "inner join batch b on s.batch_id = b.id and b.category_id = :categoryId " +
                    "inner join batch_kid_mapping bkm on bkm.batch_id = b.id and bkm.kid_id = :kidId " +
                    "inner join membership m on m.kid_id = bkm.kid_id and m.category_id = b.category_id and m.end_date > now() " +
                    "left join kid_attendance ka on ka.kid_id = bkm.kid_id and ka.session_id = s.id and ka.session_type = 'BATCH_SESSION' and ka.has_attended = false " +
                    "where s.start_date >= m.start_date and s.start_date < now() and s.is_cancelled = false ", nativeQuery = true)
    SessionsMissedCountResponse getSessionsMissedCountResponse(Long kidId, Long categoryId);

    @Query(value =  "select :kidId as kidId, s.id as sessionId, s.start_date as sessionDate, ka.attendance as attendance " +
                    "from `session` s " +
                    "inner join batch b on b.id = s.batch_id and b.category_id = :categoryId and b.is_deleted = false " +
                    "left join kid_attendance ka on ka.session_id = s.id and ka.kid_id = :kidId and ka.session_type = 'BATCH_SESSION' " +
                    "where s.start_date > :after and s.start_date < :before and s.is_cancelled = false", nativeQuery = true)
    List<KidAttendedResponse> getSessionAttendanceBetweenDates(Long kidId, Long categoryId, LocalDate after, LocalDate before);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value =  "update kid_attendance set attendance = 'ABSENT', updated_by = :updatedBy " +
                    "where session_id = :sessionId and session_type = :sessionType and kid_id in (:kidIds)", nativeQuery = true)
    void markAsAbsent(Long sessionId, String sessionType, List<Long> kidIds, String updatedBy);

    List<KidAttendance> getByKidIdInAndSessionIdAndSessionType(List<Long> kidIds, Long sessionId, SessionType sessionType);

    @Query(value =  "select bkm.kid_id as kidId, s.id as sessionId, s.start_date as sessionDate, ka.id as attendanceId, " +
                    "'BATCH_SESSION' as sessionType, ka.attendance as attendance, s.is_cancelled as isSessionCancelled " +
                    "from `session` s " +
                    "inner join batch_kid_mapping bkm on bkm.batch_id = s.batch_id " +
                    "left join kid_attendance ka on ka.session_id = s.id and ka.session_type = 'BATCH_SESSION' and bkm.kid_id = ka.kid_id " +
                    "where s.start_date = :currentDate " +
                    "union " +
                    "select askm.kid_id as kidId, ads.id as sessionId, ads.start_date as sessionDate, ka.id as attendanceId, " +
                    "'ADDITIONAL_SESSION' as sessionType, ka.attendance as attendance, ads.is_cancelled as isSessionCancelled " +
                    "from additional_session ads " +
                    "inner join additional_session_kid_mapping askm on askm.additional_session_id = ads.id " +
                    "left join kid_attendance ka on ka.session_id = ads.id and ka.session_type = 'ADDITIONAL_SESSION' and askm.kid_id = ka.kid_id " +
                    "where ads.start_date = :currentDate and ads.is_cancelled = false", nativeQuery = true)
    List<KidAttendedResponse> getSessionAttendanceResponse(LocalDate currentDate);

    @Query(value =  "select s.start_date as startDate, b.id as batchId, b.category_id as categoryId " +
                    "from kid_attendance ka inner join session s on ka.session_id = s.id " +
                    "inner join batch b on b.id = s.batch_id " +
                    "where s.start_date > :startDate and b.id = :batchId and ka.kid_id = :kidId order by s.start_date limit 1", nativeQuery = true)
    SessionResponse getNextPresentDate(Long kidId, Long batchId, LocalDate startDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value =  "update kid_attendance set attendance = 'CANCELLED', updated_by = 'SYSTEM' where id in (:ids)", nativeQuery = true)
    void markCancelled(List<Long> ids);
}
