package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.service.AdditionalSessionService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/additionalSession/kid", consumes = "application/json", produces = "application/json")
@Tag(name = "Additional Session Kid Controller")
public class AdditionalSessionKidController extends AbstractController {

    private final AdditionalSessionService additionalSessionService;

    @Autowired
    public AdditionalSessionKidController(AdditionalSessionService additionalSessionService) {
        this.additionalSessionService = additionalSessionService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Add kids to an additional session")
    @Timed
    public Response<?> addKidToAdditionalSession(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                                 @RequestParam(value = "kidId") Long kidId,
                                                 @RequestParam(value = "additionalSessionId") Long additionalSessionId){

        additionalSessionService.addKidToAdditionalSession(kidId, additionalSessionId, updatedBy);
        return getSuccessResponse(null);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(summary = "Remove kids from an additional session")
    @Timed
    public Response<?> removeKidFromAdditionalSession(@RequestParam(value = "kidId") Long kidId,
                                                      @RequestParam(value = "additionalSessionId") Long additionalSessionId){

        additionalSessionService.removeKidFromAdditionalSession(kidId, additionalSessionId);
        return getSuccessResponse(null);
    }
}
