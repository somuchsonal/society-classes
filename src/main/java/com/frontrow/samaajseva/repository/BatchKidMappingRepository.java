package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.BatchKidMapping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchKidMappingRepository extends CrudRepository<BatchKidMapping, Long>, QuerydslPredicateExecutor<BatchKidMapping> {

    void deleteByKidIdAndBatchId(Long kidId, Long batchId);

    @Query(value =  "select b.id from batch_kid_mapping bkm " +
                    "inner join batch b on bkm.batchId = b.id " +
                    "where bkm.kidId = :kidId " +
                    "and b.categoryId = (select categoryId from batch where id = :batchId)")
    Long findByKidIdAndCategoryId(Long kidId, Long batchId);
}
