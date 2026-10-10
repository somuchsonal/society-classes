package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.MembershipDbService;
import com.frontrow.samaajseva.pojo.request.SocietyMembershipPurchaseEvent;
import com.frontrow.samaajseva.pojo.view.MembershipView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class MembershipService {

    private final MembershipDbService membershipDbService;

    @Autowired
    public MembershipService(MembershipDbService membershipDbService) {
        this.membershipDbService = membershipDbService;
    }

    public MembershipView create(SocietyMembershipPurchaseEvent societyMembershipPurchaseEvent) {
        societyMembershipPurchaseEvent.setValidityInMonths(Math.round((float) societyMembershipPurchaseEvent.getValidityInMins()/(24*60*30)));
        log.info("Requested membership creation for kid Id {}, society Id {}, category Id {}, validity for months {}", societyMembershipPurchaseEvent.getKidId(), societyMembershipPurchaseEvent.getSocietyId(), societyMembershipPurchaseEvent.getCategoryId(), societyMembershipPurchaseEvent.getValidityInMonths());

        return membershipDbService.create(societyMembershipPurchaseEvent);
    }

    public List<MembershipView> getByFilter(List<Long> ids, List<Long> kidIds, List<Long> categoryIds, List<Long> societyIds, Integer validityInMonths, List<Long> orderIds, Boolean isPartlyPaid, LocalDate startDateAfter, LocalDate startDateBefore, LocalDate endDateAfter, LocalDate endDateBefore, Boolean hasNotStarted, List<Long> parentUserIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return membershipDbService.getByFilter(ids, kidIds, categoryIds, societyIds, validityInMonths, orderIds, isPartlyPaid, startDateAfter, startDateBefore, endDateAfter, endDateBefore, hasNotStarted, parentUserIds, pageSize, pageNumber, sortColumn, sortOrder);
    }
}
