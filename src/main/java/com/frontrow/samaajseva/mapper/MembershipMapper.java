package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Membership;
import com.frontrow.samaajseva.pojo.view.MembershipView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class MembershipMapper implements AbstractMapper<Membership, MembershipView> {

    private final KidMapper kidMapper;
    private final SocietyMapper societyMapper;

    @Autowired
    public MembershipMapper(KidMapper kidMapper,
                            SocietyMapper societyMapper) {
        this.kidMapper = kidMapper;
        this.societyMapper = societyMapper;
    }

    @Override
    public MembershipView getViewFromEntity(Membership membership) {
        MembershipView membershipView = new MembershipView();

        if(membership != null){
            NullAwareBeanUtils.copyPropertiesWithoutNull(membership, membershipView);
            membershipView.setKidView(kidMapper.getViewFromEntity(membership.getKid()));
            membershipView.setSocietyView(societyMapper.getViewFromEntity(membership.getSociety()));
        }

        return membershipView;
    }

    @Override
    public List<MembershipView> getViewListFromEntityList(List<Membership> membershipList) {
        List<MembershipView> membershipViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(membershipList)){
            for(Membership membership : membershipList){
                membershipViewList.add(this.getViewFromEntity(membership));
            }
        }

        return membershipViewList;
    }
}
