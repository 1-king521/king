package com.example.wyk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wyk.model.domain.Song;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SongMapper extends BaseMapper<Song> {

    /** 按收藏数排行（见 resources/mapper/SongMapper.xml） */
    List<Map<String, Object>> selectSongRankByCollectCount();
}
