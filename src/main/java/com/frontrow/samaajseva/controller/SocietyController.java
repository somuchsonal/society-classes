package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateSocietyRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.SocietyView;
import com.frontrow.samaajseva.service.SocietyService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/society", consumes = "application/json", produces = "application/json")
@Tag(name = "Society Controller")
public class SocietyController extends AbstractController {

    private final SocietyService societyService;

    @Autowired
    public SocietyController(SocietyService societyService){
        this.societyService = societyService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get society based on given filters")
    @Timed
    public Response<List<SocietyView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                   @RequestParam(value = "names", required = false) List<String> names,
                                                   @RequestParam(value = "clusterIds", required = false) List<Long> clusterIds,
                                                   @RequestParam(value = "cityIds", required = false) List<Long> cityIds,
                                                   @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                   @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                   @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                   @RequestParam(value = "sortOrder", required = false) String sortOrder){
        List<SocietyView> societyViewList = societyService.getByFilter(ids, names, clusterIds, cityIds, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<SocietyView>>) getSuccessResponse(societyViewList);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "API to create a society")
    @Timed
    public Response<SocietyView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long userId,
                                        @RequestBody @Valid CreateSocietyRequest createSocietyRequest) {
        SocietyView societyView = societyService.create(createSocietyRequest, userId);
        return (Response<SocietyView>) getSuccessResponse(societyView);
    }

}
