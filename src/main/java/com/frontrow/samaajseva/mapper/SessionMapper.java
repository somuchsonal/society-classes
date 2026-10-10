package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Session;
import com.frontrow.samaajseva.pojo.view.SessionView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class SessionMapper implements AbstractMapper<Session, SessionView> {

    private final BatchMapper batchMapper;

    @Autowired
    public SessionMapper(BatchMapper batchMapper) {
        this.batchMapper = batchMapper;
    }

    @Override
    public SessionView getViewFromEntity(Session session) {
        SessionView sessionView = new SessionView();

        if(session != null){
            NullAwareBeanUtils.copyPropertiesWithoutNull(session, sessionView);
            sessionView.setBatchView(batchMapper.getViewFromEntity(session.getBatch()));
        }

        return sessionView;
    }

    @Override
    public List<SessionView> getViewListFromEntityList(List<Session> sessionList) {
        List<SessionView> sessionViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(sessionList)){
            for (Session session: sessionList) {
                sessionViewList.add(this.getViewFromEntity(session));
            }
        }

        return sessionViewList;
    }
}
