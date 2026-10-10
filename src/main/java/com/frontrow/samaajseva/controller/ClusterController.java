package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateClusterRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.ClusterView;
import com.frontrow.samaajseva.service.ClusterService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/cluster", consumes = "application/json", produces = "application/json")
@Tag(name = "Cluster Controller")
public class ClusterController extends AbstractController {

    private final ClusterService clusterService;

    @Autowired
    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get cluster based on given filters")
    @Timed
    public Response<List<ClusterView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                   @RequestParam(value = "names", required = false) List<String> names,
                                                   @RequestParam(value = "cityIds", required = false) List<Long> cityIds,
                                                   @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                   @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                   @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                   @RequestParam(value = "sortOrder", required = false) String sortOrder) {
        List<ClusterView> clusterViewList = clusterService.getByFilter(ids, names, cityIds, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<ClusterView>>) getSuccessResponse(clusterViewList);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "API to create a cluster")
    @Timed
    public Response<ClusterView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long userId,
                                        @RequestBody @Valid CreateClusterRequest createClusterRequest) {
        ClusterView clusterView = clusterService.create(createClusterRequest, userId);
        return (Response<ClusterView>) getSuccessResponse(clusterView);
    }
}
