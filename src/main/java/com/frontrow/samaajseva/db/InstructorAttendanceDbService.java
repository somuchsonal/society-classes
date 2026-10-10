package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.AdditionalSession;
import com.frontrow.samaajseva.entity.InstructorAttendance;
import com.frontrow.samaajseva.entity.Session;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.InstructorAttendanceMapper;
import com.frontrow.samaajseva.pojo.request.MarkInstructorAttendanceRequest;
import com.frontrow.samaajseva.pojo.view.InstructorAttendanceView;
import com.frontrow.samaajseva.repository.AdditionalSessionRepository;
import com.frontrow.samaajseva.repository.InstructorAttendanceRepository;
import com.frontrow.samaajseva.repository.SessionRepository;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class InstructorAttendanceDbService {

    private final InstructorAttendanceRepository instructorAttendanceRepository;
    private final InstructorAttendanceMapper instructorAttendanceMapper;
    private final SessionRepository sessionRepository;
    private final AdditionalSessionRepository additionalSessionRepository;

    @Autowired
    public InstructorAttendanceDbService(InstructorAttendanceRepository instructorAttendanceRepository,
                                         InstructorAttendanceMapper instructorAttendanceMapper,
                                         SessionRepository sessionRepository,
                                         AdditionalSessionRepository additionalSessionRepository) {
        this.instructorAttendanceRepository = instructorAttendanceRepository;
        this.instructorAttendanceMapper = instructorAttendanceMapper;
        this.sessionRepository = sessionRepository;
        this.additionalSessionRepository = additionalSessionRepository;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public InstructorAttendanceView markInstructorAttendance(MarkInstructorAttendanceRequest markInstructorAttendanceRequest, Long updatedBy) {
        InstructorAttendance instructorAttendance = instructorAttendanceRepository.getByInstructorIdAndSessionIdAndSessionType(markInstructorAttendanceRequest.getInstructorId(), markInstructorAttendanceRequest.getSessionId(), markInstructorAttendanceRequest.getSessionType());

        if(instructorAttendance == null) {
            instructorAttendance = new InstructorAttendance();

            switch (markInstructorAttendanceRequest.getSessionType()) {
                case BATCH_SESSION -> {
                    Session session = sessionRepository.getByIdAndIsCancelled(markInstructorAttendanceRequest.getSessionId(), false);
                    if (session == null)
                        throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
                }
                case ADDITIONAL_SESSION -> {
                    AdditionalSession additionalSession = additionalSessionRepository.getByIdAndIsCancelled(markInstructorAttendanceRequest.getSessionId(), false);
                    if (additionalSession == null)
                        throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
                }
            }
        }

        NullAwareBeanUtils.copyPropertiesWithoutNull(markInstructorAttendanceRequest, instructorAttendance);
        instructorAttendance.setUpdatedBy(updatedBy.toString());

        instructorAttendanceRepository.save(instructorAttendance);

        return instructorAttendanceMapper.getViewFromEntity(instructorAttendance);
    }
}
