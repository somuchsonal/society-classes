package com.frontrow.samaajseva.service;

import com.frontrow.samaajseva.db.VideoDbService;
import com.frontrow.samaajseva.db.VideoKidMappingDbService;
import com.frontrow.samaajseva.pojo.request.CreateVideoRequest;
import com.frontrow.samaajseva.pojo.view.VideoView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VideoService {

    private final VideoDbService videoDbService;
    private final VideoKidMappingDbService videoKidMappingDbService;

    @Autowired
    public VideoService(VideoDbService videoDbService,
                        VideoKidMappingDbService videoKidMappingDbService) {
        this.videoDbService = videoDbService;
        this.videoKidMappingDbService = videoKidMappingDbService;
    }

    public List<VideoView> getByFilter(List<Long> ids, List<Long> updatedByIds, Date createdAfter, Date createdBefore, List<Long> sessionIds, List<Long> batchIds, List<Long> kidIds, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        return videoDbService.getByFilter(ids, updatedByIds, createdAfter, createdBefore, sessionIds, batchIds, kidIds, pageSize, pageNumber, sortColumn, sortOrder);
    }

    public VideoView create(CreateVideoRequest createVideoRequest, Long updatedBy) {
        return videoDbService.create(createVideoRequest, updatedBy);
    }

    public void createVideoKidMapping(Long videoId, List<Long> kidIds, Long updatedBy) {
        videoKidMappingDbService.create(videoId, kidIds, updatedBy);
    }

    public void removeVideoKidMapping(Long videoId, List<Long> kidIds) {
        videoKidMappingDbService.removeMappings(videoId, kidIds);
    }
}
