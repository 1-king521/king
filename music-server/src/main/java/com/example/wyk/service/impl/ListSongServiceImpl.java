package com.example.wyk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wyk.common.R;
import com.example.wyk.mapper.ListSongMapper;
import com.example.wyk.mapper.SongMapper;
import com.example.wyk.model.domain.ListSong;
import com.example.wyk.model.domain.Song;
import com.example.wyk.model.request.ListSongRequest;
import com.example.wyk.service.ListSongService;
import com.example.wyk.service.SongSingerPicSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ListSongServiceImpl extends ServiceImpl<ListSongMapper, ListSong> implements ListSongService {

    private final ListSongMapper listSongMapper;

    private final SongMapper songMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final SongSingerPicSupport songSingerPicSupport;

    private void evictSongListSongsCache(Integer songListId) {
        if (songListId != null) {
            stringRedisTemplate.delete("songList:songs:" + songListId);
        }
    }

    @Override
    public List<ListSong> allListSong() {
        return listSongMapper.selectList(null);
    }

    @Override
    public R updateListSongMsg(ListSongRequest updateListSongRequest) {
        ListSong listSong = new ListSong();
        BeanUtils.copyProperties(updateListSongRequest, listSong);
        if (listSongMapper.updateById(listSong) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R deleteListSong(Integer songListId, Integer songId) {
        QueryWrapper<ListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_sheet_id", songListId).eq("song_id", songId);
        if (listSongMapper.delete(queryWrapper) > 0) {
            evictSongListSongsCache(songListId);
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @Override
    public R addListSong(ListSongRequest addListSongRequest) {
        ListSong listSong = new ListSong();
        BeanUtils.copyProperties(addListSongRequest, listSong);
        Integer songListId = listSong.getSongListId();
        // 1. 先插入数据库
        if (listSongMapper.insert(listSong) > 0) {

            // 2. 插入成功 → 删除这个歌单的缓存（关键！）
            evictSongListSongsCache(songListId);

            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @Override
    public R listSongOfSongId(Integer songListId) {
        QueryWrapper<ListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_sheet_id", songListId);
        return R.success("查询成功", listSongMapper.selectList(queryWrapper));
    }

    @Override
    public R songDetailOfSongListId(Integer songListId) {
        // ===================== 1. 先从 Redis 拿缓存 =====================
        String cacheKey = "songList:songs:" + songListId;  // 缓存key
        String cacheData = stringRedisTemplate.opsForValue().get(cacheKey);

        // 如果缓存里有数据，直接返回！不查数据库！
        if (cacheData != null) {
            //判断是否是空值 如果是空值 直接返回
            if (!StringUtils.hasText(cacheData)) {
                return R.error("歌单不存在");
            }
            List<Map<String, Object>> songList = JSON.parseObject(cacheData, new TypeReference<List<Map<String, Object>>>() {});
            songSingerPicSupport.enrichSongMaps(songList);
            return R.success("查询成功(缓存)", songList);
        }
        //判断是否是空值 如果是空值 直接返回
//        if (!StringUtils.hasText(cacheData)) {
//            return R.error("歌单不存在");
//        }

        // ===================== 2. 缓存没有，才查数据库 =====================
        QueryWrapper<ListSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_sheet_id", songListId);
        List<ListSong> listSongList = listSongMapper.selectList(queryWrapper);

        List<Map<String, Object>> songList = new ArrayList<>();
        for (ListSong listSong : listSongList) {
            Song song = songMapper.selectById(listSong.getSongId());
            if (song != null) {
                String name = song.getName();
                String singerName = "";
                String songName = "";
                if (name != null) {
                    String[] splitName = name.split("-", 2);
                    singerName = splitName[0].trim();
                    songName = splitName.length > 1 ? splitName[1].trim() : splitName[0].trim();
                }

                Map<String, Object> songMap = new LinkedHashMap<>();
                songMap.put("id", song.getId());
                songMap.put("singerId", song.getSingerId());
                songMap.put("name", song.getName());
                songMap.put("songName", songName);
                songMap.put("singerName", singerName);
                songMap.put("introduction", song.getIntroduction());
                songMap.put("createTime", song.getCreateTime());
                songMap.put("updateTime", song.getUpdateTime());
                songMap.put("pic", song.getPic());
                songMap.put("lyric", song.getLyric());
                songMap.put("duration", song.getDuration());
                songMap.put("url", song.getUrl());
                songList.add(songMap);
            }
        }
        songSingerPicSupport.enrichSongMaps(songList);
        //如果查询结果为空，返回错误信息，把空值存到redis(缓存穿透)
        if (songList.isEmpty()){
            stringRedisTemplate.opsForValue().set(cacheKey, " ", 10, TimeUnit.MINUTES);
            return R.error("查询结果为空");
        }

        // ===================== 3. 把查询结果存入 Redis（缓存10分钟） =====================
        String jsonResult = JSON.toJSONString(songList);
        stringRedisTemplate.opsForValue().set(cacheKey, jsonResult, 10, TimeUnit.MINUTES);

        return R.success("查询成功", songList);
    }

}
