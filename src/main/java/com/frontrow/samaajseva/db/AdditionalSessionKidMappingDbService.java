package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.config.properties.MiscProperties;
import com.frontrow.samaajseva.entity.AdditionalSession;
import com.frontrow.samaajseva.entity.AdditionalSessionKidMapping;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.repository.AdditionalSessionKidMappingRepository;
import com.frontrow.samaajseva.repository.AdditionalSessionRepository;
import com.frontrow.samaajseva.repository.KidAttendanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AdditionalSessionKidMappingDbService {

    private final AdditionalSessionKidMappingRepository additionalSessionKidMappingRepository;
    private final KidAttendanceRepository kidAttendanceRepository;
    private final AdditionalSessionRepository additionalSessionRepository;
    private final AdditionalSessionDbService additionalSessionDbService;
    private final MiscProperties miscProperties;

    @Autowired
    public AdditionalSessionKidMappingDbService(AdditionalSessionKidMappingRepository additionalSessionKidMappingRepository,
                                                KidAttendanceRepository kidAttendanceRepository, 
                                                AdditionalSessionRepository additionalSessionRepository,
                                                AdditionalSessionDbService additionalSessionDbService, 
                                                MiscProperties miscProperties) {
        this.additionalSessionKidMappingRepository = additionalSessionKidMappingRepository;
        this.kidAttendanceRepository = kidAttendanceRepository;
        this.additionalSessionDbService = additionalSessionDbService;
        this.additionalSessionRepository = additionalSessionRepository;
        this.miscProperties = miscProperties;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addKidToAdditionalSession(Long kidId, Long additionalSessionId, Long updatedBy) {
        AdditionalSession additionalSession = additionalSessionRepository.getByIdAndIsCancelled(additionalSessionId, false);
        if(additionalSession == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
        
        var sessionsMissedResponse = kidAttendanceRepository.getSessionsMissedCountResponse(kidId, additionalSession.getCategoryId());
        
        if(sessionsMissedResponse.getMissedCount() <= 0)
            throw new CustomException(ErrorCodes.NOT_ELIGIBLE_FOR_COMPENSATORY_CLASS);

        var additionalSessionViewList = additionalSessionDbService.getByFilter(null, null, null, null,
                                                                                                         Collections.singletonList(additionalSession.getCategoryId()), false, 
                                                                                                         Collections.singletonList(kidId), null, null, null, null);
        if( additionalSessionViewList.size() >= sessionsMissedResponse.getMissedCount())
            throw new CustomException(ErrorCodes.NOT_ELIGIBLE_FOR_COMPENSATORY_CLASS);

        LocalDate membershipMonthStartDate =  sessionsMissedResponse.getMembershipStartDate();
        
        while(membershipMonthStartDate.plusMonths(1).isBefore(LocalDate.now()))
            membershipMonthStartDate = membershipMonthStartDate.plusMonths(1);
        
        LocalDate monthStartDate = membershipMonthStartDate;
        
        additionalSessionViewList =  additionalSessionViewList.stream().filter(adSession -> adSession.getStartDate().isAfter(monthStartDate)).collect(Collectors.toList());
        if(additionalSessionViewList.size() >= miscProperties.getCompensatoryClassesPerMonth())
            throw new CustomException(ErrorCodes.NOT_ELIGIBLE_FOR_COMPENSATORY_CLASS);
        
        //Eligibility for getting an additional session is proven at this point

        AdditionalSessionKidMapping additionalSessionKidMapping = new AdditionalSessionKidMapping();
        additionalSessionKidMapping.setAdditionalSession(additionalSession);
        additionalSessionKidMapping.setKidId(kidId);
        additionalSessionKidMapping.setUpdatedBy(updatedBy.toString());
        
        additionalSessionKidMappingRepository.save(additionalSessionKidMapping);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeKidFromAdditionalSession(Long kidId, Long additionalSessionId) {
        additionalSessionKidMappingRepository.deleteByKidIdAndAdditionalSessionId(kidId, additionalSessionId);
    }
}
