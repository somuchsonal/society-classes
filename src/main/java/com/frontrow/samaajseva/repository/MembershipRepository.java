package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.Membership;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MembershipRepository extends CrudRepository<Membership, Long>, QuerydslPredicateExecutor<Membership> {

    @NotNull
    @Override
    List<Membership> findAll(@NotNull Predicate predicate);

    List<Membership> getByKidIdAndCategoryIdAndSocietyId(Long kidId, Long categoryId, Long societyId);

    List<Membership> getByIdIn(List<Long> ids);

    List<Membership> getByKidIdAndCategoryId(Long kidId, Long categoryId);

    List<Membership> getByKidIdInAndCategoryIdAndStartDate(List<Long> kidId, Long categoryId, LocalDate startDate);

}
