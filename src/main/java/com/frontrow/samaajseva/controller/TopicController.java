package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.enums.CompletionType;
import com.frontrow.samaajseva.enums.ContentType;
import com.frontrow.samaajseva.enums.Level;
import com.frontrow.samaajseva.enums.TopicType;
import com.frontrow.samaajseva.pojo.request.CreateTopicRequest;
import com.frontrow.samaajseva.pojo.request.UpdateTopicRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.TopicView;
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
@RequestMapping(value = "/topic", consumes = "application/json", produces = "application/json")
@Tag(name = "Topic Controller")
public class TopicController extends AbstractController {

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get topics based on given filters")
    @Timed
    public Response<List<TopicView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                 @RequestParam(value = "topicTypes", required = false) List<TopicType> topicTypes,
                                                 @RequestParam(value = "contentTypes", required = false) List<ContentType> contentTypes,
                                                 @RequestParam(value = "completionTypes", required = false) List<CompletionType> completionTypes,
                                                 @RequestParam(value = "chapterIds", required = false) List<Long> chapterIds,
                                                 @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                 @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
                                                 @RequestParam(value = "levels", required = false) List<Level> levels,
                                                 @RequestParam(value = "subLevels", required = false) List<Integer> subLevels,
                                                 @RequestParam(value = "subSubLevels", required = false) List<Integer> subSubLevels,
                                                 @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                 @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                 @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                 @RequestParam(value = "sortOrder", required = false) String sortOrder){

        List<TopicView> topicViewList = topicService.getByFilter(ids, topicTypes, contentTypes, completionTypes, chapterIds, isDeleted, categoryIds, levels, subLevels, subSubLevels, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<TopicView>>) getSuccessResponse(topicViewList);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Create a topic based on given details")
    @Timed
    public Response<TopicView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long userId,
                                      @RequestBody @Valid CreateTopicRequest createTopicRequest) {
        TopicView topicView = topicService.create(createTopicRequest, userId);
        return (Response<TopicView>) getSuccessResponse(topicView);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @Operation(summary = "Update a topic based on given details")
    @Timed
    public Response<TopicView> update(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                      @RequestBody @Valid UpdateTopicRequest updateTopicRequest) {
        TopicView topicView = topicService.update(updateTopicRequest, updatedBy);
        return (Response<TopicView>) getSuccessResponse(topicView);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(method = "Mark a topic as deleted based on given id")
    @Timed
    public Response<?> delete(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                              @RequestParam(value = "id") Long id){

        topicService.delete(id, updatedBy);
        return getSuccessResponse(null);
    }

}
