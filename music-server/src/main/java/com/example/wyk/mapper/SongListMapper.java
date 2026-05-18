package com.example.wyk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wyk.model.domain.SongList;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository

public interface SongListMapper extends BaseMapper<SongList> {

    List<SongList> selectRecommendedSongLists(@Param("limit") Integer limit);

    /** 按歌单被收藏次数排行（type=1），见 SongListMapper.xml */
    List<Map<String, Object>> selectSongListRankByCollectCount();
}
