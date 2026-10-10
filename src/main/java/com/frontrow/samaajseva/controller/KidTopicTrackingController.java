package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.request.MarkTopicAsCompleteRequest;
import com.frontrow.samaajseva.pojo.view.CompletionPercentageView;
import com.frontrow.samaajseva.pojo.view.CurrentLevelView;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.KidTopicTrackingView;
import com.frontrow.samaajseva.service.TopicService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/topic/kid", consumes = "application/json", produces = "application/json")
@Tag(name = "Kid Topic Tracking Controller")
public class KidTopicTrackingController extends AbstractController {

    private final TopicService topicService;

    @Autowired
    public KidTopicTrackingController(TopicService topicService) {
        this.topicService = topicService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get Topic Completion based on given filters")
    @Timed
    public Response<List<KidTopicTrackingView>> getCompletionByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                                      @RequestParam(value = "topicIds", required = false) List<Long> topicIds,
                                                                      @RequestParam(value = "kidIds", required = false) List<Long> kidIds,
                                                                      @RequestParam(value = "sessionIds", required = false) List<Long> sessionIds,
                                                                      @RequestParam(value = "sessionTypes", required = false) List<SessionType> sessionTypes,
                                                                      @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                                      @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                                      @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                                      @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        List<KidTopicTrackingView> kidTopicTrackingViewList = topicService.getCompletionByFilter(ids, topicIds, kidIds, sessionIds, sessionTypes, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<KidTopicTrackingView>>) getSuccessResponse(kidTopicTrackingViewList);
    }

    @RequestMapping(value = "/markComplete", method = RequestMethod.POST)
    @Operation(summary = "Mark topic as complete")
    @Timed
    public Response<?> markTopicAsComplete(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                           @RequestBody @Valid MarkTopicAsCompleteRequest markTopicAsCompleteRequest) {

        topicService.markTopicAsComplete(markTopicAsCompleteRequest, updatedBy);
        return getSuccessResponse(null);
    }

    @RequestMapping(value = "/markIncomplete", method = RequestMethod.DELETE)
    @Operation(summary = "Mark topic as incomplete")
    @Timed
    public Response<?> markTopicAsIncomplete(@RequestParam(value = "kidId") Long kidId,
                                             @RequestParam(value = "topicId") Long topicId) {
        topicService.deleteTopicCompletion(kidId, topicId);
        return getSuccessResponse(null);
    }

    @RequestMapping(value = "/currentLevel", method = RequestMethod.GET)
    @Operation(summary = "Get current Level for a kid for a category")
    @Timed
    public Response<CurrentLevelView> getCurrentLevel(@RequestParam(value = "kidId") Long kidId,
                                                      @RequestParam(value = "categoryId") Long categoryId) {
        CurrentLevelView currentLevel = topicService.getCurrentLevel(kidId, categoryId);
        return (Response<CurrentLevelView>) getSuccessResponse(currentLevel);
    }

    @RequestMapping(value = "/completionPercentage", method = RequestMethod.GET)
    @Operation(summary = "Get current Level for a kid for a category")
    @Timed
    public Response<CompletionPercentageView> getCompletionPercentage(@RequestParam(value = "kidId") Long kidId,
                                                                      @RequestParam(value = "categoryId") Long categoryId) {
        CompletionPercentageView completionPercentage = topicService.getCompletionPercentage(kidId, categoryId);
        return (Response<CompletionPercentageView>) getSuccessResponse(completionPercentage);
    }

}
