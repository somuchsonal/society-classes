package com.frontrow.samaajseva.repository;

import com.frontrow.samaajseva.entity.InstructorAttendance;
import com.frontrow.samaajseva.enums.SessionType;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorAttendanceRepository extends CrudRepository<InstructorAttendance, Long>, QuerydslPredicateExecutor<InstructorAttendance> {

    InstructorAttendance getByInstructorIdAndSessionIdAndSessionType(Long instructorId, Long sessionId, SessionType sessionType);
}
