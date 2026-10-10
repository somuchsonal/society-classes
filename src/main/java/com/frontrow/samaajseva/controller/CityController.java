package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateCityRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.CityView;
import com.frontrow.samaajseva.service.CityService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/city", consumes = "application/json", produces = "application/json")
@Tag(name = "City Controller")
public class CityController extends AbstractController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get city based on given filters")
    @Timed
    public Response<List<CityView>> getByFilter(@RequestHeader(value = Constants.HEADER_USER_ID) Long userId,
                                                @RequestParam(value = "ids", required = false) List<Long> ids,
                                                @RequestParam(value = "names", required = false) List<String> names,
                                                @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                @RequestParam(value = "sortOrder", required = false) String sortOrder) {
        List<CityView> cityViewList = cityService.getByFilter(ids, names, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<CityView>>) getSuccessResponse(cityViewList);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "API to create a city")
    @Timed
    public Response<CityView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long userId,
                                     @RequestBody @Valid CreateCityRequest createCityRequest) {
        CityView cityView = cityService.create(createCityRequest, userId);
        return (Response<CityView>) getSuccessResponse(cityView);
    }
}
