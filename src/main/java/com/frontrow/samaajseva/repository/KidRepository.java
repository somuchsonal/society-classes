package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.Kid;
import com.frontrow.samaajseva.pojo.response.KidSessionMembershipResponse;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface KidRepository extends CrudRepository<Kid, Long>, QuerydslPredicateExecutor<Kid> {

    @NotNull
    @Override
    List<Kid> findAll(@NotNull Predicate predicate);

    @Query( value = "select k.* from kid k " +
                    "inner join batch_kid_mapping bkm on bkm.kid_id = k.id and bkm.batch_id = :batchId " +
                    "inner join membership m on m.kid_id = k.id and (m.end_date > :currentDate or m.end_date is null " +
                    "or (m.is_partly_paid is true and m.end_date > :currentDateWithBuffer)) " +
                    "inner join batch b on b.id = bkm.batch_id ", nativeQuery = true)
    List<Kid> getActiveKidsFromBatch(Long batchId, LocalDate currentDate, LocalDate currentDateWithBuffer);

    @Query( value = "select k.* from kid k inner join membership m on k.id = m.kid_id and m.end_date is null " +
                    "left join batch_kid_mapping bkm on bkm.kid_id = k.id " +
                    "where bkm.id is null group by k.id", nativeQuery = true)
    List<Kid> getUnassignedKids();

    @Query(value =  "select bkm.kid_id as kidId, s.id as sessionId, 'BATCH_SESSION' as sessionType, s.start_date as sessionDate, activeM.id as activeMembershipId, " +
                    "unusedM.id as unusedMembershipId, unusedM.validity_in_months as validityInMonths " +
                    "from `session` s " +
                    "inner join batch_kid_mapping bkm on bkm.batch_id = s.batch_id " +
                    "inner join batch b on s.batch_id = b.id " +
                    "left join membership activeM on bkm.kid_id = activeM.kid_id and activeM.category_id = b.category_id and activeM.end_date > now() " +
                    "left join membership unusedM on bkm.kid_id = unusedM.kid_id and unusedM.category_id = b.category_id and unusedM.end_date is null " +
                    "where bkm.kid_id in (:kidIds) and s.id = :sessionId and s.is_cancelled = false and b.is_deleted = false " +
                    "union " +
                    "select askm.kid_id as kidId, ads.id as sessionId, 'ADDITIONAL_SESSION' as sessionType, ads.start_date as sessionDate, activeM.id as activeMembershipId, " +
                    "unusedM.id as unusedMembershipId, unusedM.validity_in_months as validityInMonths " +
                    "from additional_session ads " +
                    "inner join additional_session_kid_mapping askm on askm.additional_session_id = ads.id " +
                    "left join membership activeM on askm.kid_id = activeM.kid_id and activeM.category_id = ads.category_id and activeM.end_date > now() " +
                    "left join membership unusedM on askm.kid_id = unusedM.kid_id and unusedM.category_id = ads.category_id and unusedM.end_date is null " +
                    "where askm.kid_id in (:kidIds) and ads.id = :sessionId and ads.is_cancelled = false ", nativeQuery = true)
    List<KidSessionMembershipResponse> getSessionMembershipsData(List<Long> kidIds, Long sessionId);
}
