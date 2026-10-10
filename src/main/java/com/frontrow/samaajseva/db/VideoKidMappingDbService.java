package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.entity.Kid;
import com.frontrow.samaajseva.entity.Video;
import com.frontrow.samaajseva.entity.VideoKidMapping;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.repository.KidRepository;
import com.frontrow.samaajseva.repository.VideoKidMappingRepository;
import com.frontrow.samaajseva.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VideoKidMappingDbService {

    private final VideoKidMappingRepository videoKidMappingRepository;
    private final VideoRepository videoRepository;
    private final KidRepository kidRepository;

    @Autowired
    public VideoKidMappingDbService(VideoKidMappingRepository videoKidMappingRepository,
                                    VideoRepository videoRepository,
                                    KidRepository kidRepository) {
        this.videoKidMappingRepository = videoKidMappingRepository;
        this.videoRepository = videoRepository;
        this.kidRepository = kidRepository;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void createWithVideo(Video video, List<Long> kidIds, Long updatedBy) {
        List<VideoKidMapping> videoKidMappingList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(kidIds)) {
            for (Long kidId : kidIds) {
                Kid kid = kidRepository.findById(kidId).orElse(null);
                if(kid == null)
                    continue;

                VideoKidMapping videoKidMapping = new VideoKidMapping();
                videoKidMapping.setKid(kid);
                videoKidMapping.setVideo(video);
                videoKidMapping.setUpdatedBy(updatedBy.toString());

                videoKidMappingList.add(videoKidMapping);
            }
        }

        videoKidMappingRepository.saveAll(videoKidMappingList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void create(Long videoId, List<Long> kidIds, Long updatedBy) {
        Video video = videoRepository.findById(videoId).orElse(null);

        if(video == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        this.createWithVideo(video, kidIds, updatedBy);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeMappings(Long videoId, List<Long> kidIds) {
        videoKidMappingRepository.deleteByVideoIdAndKidIdIn(videoId, kidIds);
    }
}
