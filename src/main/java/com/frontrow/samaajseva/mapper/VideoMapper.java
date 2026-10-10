package com.frontrow.samaajseva.mapper;

import com.frontrow.samaajseva.entity.Video;
import com.frontrow.samaajseva.pojo.view.VideoView;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class VideoMapper implements AbstractMapper<Video, VideoView> {

    private final SessionMapper sessionMapper;

    @Autowired
    public VideoMapper(SessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
    }

    @Override
    public VideoView getViewFromEntity(Video video) {
        VideoView videoView = new VideoView();

        if(video != null) {
            NullAwareBeanUtils.copyPropertiesWithoutNull(video, videoView);
            videoView.setSessionView(sessionMapper.getViewFromEntity(video.getSession()));
        }

        return videoView;
    }

    @Override
    public List<VideoView> getViewListFromEntityList(List<Video> videoList) {
        List<VideoView> videoViewList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(videoList)) {
            for (Video video : videoList) {
                videoViewList.add(this.getViewFromEntity(video));
            }
        }

        return videoViewList;
    }
}
