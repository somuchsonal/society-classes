package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.response.Response;
import com.frontrow.samaajseva.service.VideoService;
import com.frontrow.samaajseva.utils.Constants;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/video/kid", produces = "application/json", consumes = "application/json")
@Tag(name = "Video Kid Controller")
public class VideoKidController extends AbstractController {

    private final VideoService videoService;

    @Autowired
    public VideoKidController(VideoService videoService) {
        this.videoService = videoService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(summary = "Create video kid mapping")
    @Timed
    public Response<?> create(@RequestHeader(value = Constants.HEADER_USER_ID) Long updatedBy,
                              @RequestParam(value = "videoId") Long videoId,
                              @RequestParam(value = "kidIds") List<Long> kidIds) {
        videoService.createVideoKidMapping(videoId, kidIds, updatedBy);
        return getSuccessResponse(null);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(summary = "Delete video kid mapping")
    @Timed
    public Response<?> removeMapping(@RequestParam(value = "videoId") Long videoId,
                                     @RequestParam(value = "kidIds") List<Long> kidIds) {
        videoService.removeVideoKidMapping(videoId, kidIds);
        return getSuccessResponse(null);
    }

}
