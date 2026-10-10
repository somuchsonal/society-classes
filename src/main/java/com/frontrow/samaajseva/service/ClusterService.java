package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.ClusterDbService;
import com.frontrow.samaajseva.pojo.request.CreateClusterRequest;
import com.frontrow.samaajseva.pojo.view.ClusterView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ClusterService {

    private final ClusterDbService clusterDbService;

    @Autowired
    public ClusterService(ClusterDbService clusterDbService) {
        this.clusterDbService = clusterDbService;
    }

    public List<ClusterView> getByFilter(List<Long> ids, List<String> names, List<Long> cityIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return clusterDbService.getByFilter(ids, names, cityIds, pageSize, pageNumber, sortColumn, sortOrder);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ClusterView create(CreateClusterRequest createClusterRequest, Long updatedBy) {
        return clusterDbService.create(createClusterRequest, updatedBy);

    }
}
