package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.VideoKidMapping;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoKidMappingRepository extends CrudRepository<VideoKidMapping, Long>, QuerydslPredicateExecutor<VideoKidMapping> {

    void deleteByVideoIdAndKidIdIn(Long videoId, List<Long> kidId);
}
