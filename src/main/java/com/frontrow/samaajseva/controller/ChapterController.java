package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.enums.ChapterType;
import com.frontrow.samaajseva.enums.Level;
import com.frontrow.samaajseva.enums.SessionType;
import com.frontrow.samaajseva.pojo.request.CreateChapterRequest;
import com.frontrow.samaajseva.pojo.request.UpdateChapterRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.ChapterView;
import com.frontrow.samaajseva.pojo.view.KidReportCompletionView;
import com.frontrow.samaajseva.service.ChapterService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/chapter", consumes = "application/json", produces = "application/json")
@Tag(name = "Chapter Controller")
public class ChapterController extends AbstractController {

    private final ChapterService chapterService;

    @Autowired
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get chapters based on given filters")
    @Timed
    public Response<List<ChapterView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                   @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                   @RequestParam(value = "levels", required = false) List<Level> levels,
                                                   @RequestParam(value = "subLevels", required = false) List<Integer> subLevels,
                                                   @RequestParam(value = "subSubLevels", required = false) List<Integer> subSubLevels,
                                                   @RequestParam(value = "chapterTypes", required = false) List<ChapterType> chapterTypes,
                                                   @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
                                                   @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                   @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                   @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                   @RequestParam(value = "sortOrder", required = false) String sortOrder){
        List<ChapterView> chapterViewList = chapterService.getByFilter(ids, categoryIds, levels, subLevels, subSubLevels, chapterTypes, isDeleted, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<ChapterView>>) getSuccessResponse(chapterViewList);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Create a chapter based on given details")
    @Timed
    public Response<ChapterView> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                        @RequestBody @Valid CreateChapterRequest createChapterRequest) {
        ChapterView chapterView = chapterService.create(createChapterRequest, updatedBy);
        return (Response<ChapterView>) getSuccessResponse(chapterView);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @Operation(summary = "Update a chapter based on given details")
    @Timed
    public Response<ChapterView> update(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                        @RequestBody @Valid UpdateChapterRequest updateChapterRequest) {
        ChapterView chapterView = chapterService.update(updateChapterRequest, updatedBy);
        return (Response<ChapterView>) getSuccessResponse(chapterView);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(method = "Mark a chapter as deleted based on given id")
    @Timed
    public Response<?> delete(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                              @RequestParam(value = "id") Long id){

        chapterService.delete(id, updatedBy);
        return getSuccessResponse(null);
    }

    @RequestMapping(value = "/getCompletedReport", method = RequestMethod.GET)
    @Operation(method = "Get completed report based on filters")
    @Timed
    public Response<List<KidReportCompletionView>> getCompletedReport(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                                      @RequestParam(value = "chapterIds", required = false) List<Long> chapterIds,
                                                                      @RequestParam(value = "kidIds", required = false) List<Long> kidIds,
                                                                      @RequestParam(value = "sessionIds", required = false) List<Long> sessionIds,
                                                                      @RequestParam(value = "sessionTypes", required = false) List<SessionType> sessionTypes,
                                                                      @RequestParam(value = "createdAfter", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") Date createdAfter,
                                                                      @RequestParam(value = "createdBefore", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") Date createdBefore,
                                                                      @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                                      @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                                      @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                                      @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        List<KidReportCompletionView> kidReportCompletionViewList = chapterService.getCompletedReport(ids, chapterIds, kidIds, sessionIds, sessionTypes, createdAfter, createdBefore, pageSize, pageNumber, sortColumn, sortOrder);

        return (Response<List<KidReportCompletionView>>) getSuccessResponse(kidReportCompletionViewList);
    }
}
