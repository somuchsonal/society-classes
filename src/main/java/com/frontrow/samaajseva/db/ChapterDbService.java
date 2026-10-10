package com.frontrow.samaajseva.db;

import com.frontrow.samaajseva.comparator.ChapterComparator;
import com.frontrow.samaajseva.entity.Chapter;
import com.frontrow.samaajseva.entity.QChapter;
import com.frontrow.samaajseva.enums.ChapterType;
import com.frontrow.samaajseva.enums.Level;
import com.frontrow.samaajseva.exception.CustomException;
import com.frontrow.samaajseva.exception.ErrorCodes;
import com.frontrow.samaajseva.mapper.ChapterMapper;
import com.frontrow.samaajseva.pojo.request.CreateChapterRequest;
import com.frontrow.samaajseva.pojo.request.UpdateChapterRequest;
import com.frontrow.samaajseva.pojo.view.ChapterView;
import com.frontrow.samaajseva.repository.ChapterRepository;
import com.frontrow.samaajseva.utils.CommonFunctions;
import com.frontrow.samaajseva.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChapterDbService {

    private final ChapterRepository chapterRepository;
    private final ChapterMapper chapterMapper;

    @Autowired
    public ChapterDbService(ChapterRepository chapterRepository,
                            ChapterMapper chapterMapper) {
        this.chapterRepository = chapterRepository;
        this.chapterMapper = chapterMapper;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<ChapterView> getByFilter(List<Long> ids, List<Long> categoryIds, List<Level> levels, List<Integer> subLevels, List<Integer> subSubLevels, List<ChapterType> chapterTypes, Boolean isDeleted, Long pageSize, Long pageNumber, String sortColumn, String sortOrder) {
        QChapter qChapter = QChapter.chapter;
        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();

        if(!CollectionUtils.isEmpty(ids))
            booleanExpression = booleanExpression.and(qChapter.id.in(ids));

        if(!CollectionUtils.isEmpty(categoryIds))
            booleanExpression = booleanExpression.and(qChapter.categoryId.in(categoryIds));

        if(!CollectionUtils.isEmpty(levels))
            booleanExpression = booleanExpression.and(qChapter.level.in(levels));

        if(!CollectionUtils.isEmpty(subLevels))
            booleanExpression = booleanExpression.and(qChapter.subLevel.in(subLevels));

        if(!CollectionUtils.isEmpty(subSubLevels))
            booleanExpression = booleanExpression.and(qChapter.subSubLevel.in(subSubLevels));

        if(!CollectionUtils.isEmpty(chapterTypes))
            booleanExpression = booleanExpression.and(qChapter.chapterType.in(chapterTypes));

        if(isDeleted != null)
            booleanExpression = booleanExpression.and(qChapter.isDeleted.eq(isDeleted));

        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Chapter> chapterList =  pageable == null ?
                chapterRepository.findAll(booleanExpression)
                : chapterRepository.findAll(booleanExpression, pageable).getContent();

        chapterList = new ArrayList<>(chapterList);
        chapterList.sort(new ChapterComparator());

        return chapterMapper.getViewListFromEntityList(chapterList);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ChapterView create(CreateChapterRequest createChapterRequest, Long updatedBy) {
        Chapter chapter = new Chapter();

        NullAwareBeanUtils.copyPropertiesWithoutNull(createChapterRequest, chapter);
        chapter.setUpdatedBy(updatedBy.toString());

        chapterRepository.save(chapter);

        return chapterMapper.getViewFromEntity(chapter);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ChapterView update(UpdateChapterRequest updateChapterRequest, Long updatedBy) {
        Chapter chapter = chapterRepository.getByIdAndIsDeleted(updateChapterRequest.getChapterId(), false);
        if(chapter == null)
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);

        NullAwareBeanUtils.copyPropertiesWithoutNull(updateChapterRequest, chapter);
        chapter.setUpdatedBy(updatedBy.toString());

        return chapterMapper.getViewFromEntity(chapter);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void markAsDeleted(Long id, Long updatedBy) {
        chapterRepository.markAsDeleted(id, updatedBy.toString());
    }
}
