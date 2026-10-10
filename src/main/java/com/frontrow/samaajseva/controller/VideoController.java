package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.request.CreateVideoRequest;
import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.pojo.view.VideoView;
import com.frontrow.samaajseva.service.VideoService;
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
@RequestMapping(value = "/video", consumes = "application/json", produces = "application/json")
@Tag(name = "Video Controller")
public class VideoController extends AbstractController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get videos by filter")
    @Timed
    public Response<List<VideoView>> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                 @RequestParam(value = "updatedByIds", required = false) List<Long> updatedByIds,
                                                 @RequestParam(value = "createdAfter", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") Date createdAfter,
                                                 @RequestParam(value = "createdBefore", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") Date createdBefore,
                                                 @RequestParam(value = "sessionIds", required = false) List<Long> sessionIds,
                                                 @RequestParam(value = "batchIds", required = false) List<Long> batchIds,
                                                 @RequestParam(value = "kidIds", required = false) List<Long> kidIds,
                                                 @RequestParam(value = "pageSize", required = false) Long pageSize,
                                                 @RequestParam(value = "pageNumber", required = false) Long pageNumber,
                                                 @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                                 @RequestParam(value = "sortOrder", required = false) String sortOrder){
        List<VideoView> videoViewList = videoService.getByFilter(ids, updatedByIds, createdAfter, createdBefore, sessionIds, batchIds, kidIds, pageSize, pageNumber, sortColumn, sortOrder);
        return (Response<List<VideoView>>) getSuccessResponse(videoViewList);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Store video and create video kid mapping")
    @Timed
    public Response<VideoView> createVideo(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                                           @RequestBody @Valid CreateVideoRequest createVideoRequest) {
        VideoView videoView = videoService.create(createVideoRequest, updatedBy);
        return (Response<VideoView>) getSuccessResponse(videoView);
    }
}
