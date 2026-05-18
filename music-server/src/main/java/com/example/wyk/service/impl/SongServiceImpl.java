package com.example.wyk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wyk.common.R;
import com.example.wyk.mapper.SongMapper;
import com.example.wyk.model.domain.Song;
import com.example.wyk.model.request.SongRequest;
import com.example.wyk.service.MinioUploadService;
import com.example.wyk.service.SongService;
import com.example.wyk.service.SongSingerPicSupport;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    private final SongMapper songMapper;

    private final SongSingerPicSupport songSingerPicSupport;

    private final MinioUploadService minioUploadService;

    private final MinioClient minioClient;

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public R allSong() {
        return R.success(null, songMapper.selectList(null));
    }

    @Override
    public R pageSong(Integer page, Integer size) {
        Page<Song> pageInfo = new Page<>(page, size);
        songMapper.selectPage(pageInfo, null);
        Map<String, Object> data = new HashMap<>();
        data.put("records", pageInfo.getRecords());
        data.put("total", pageInfo.getTotal());
        data.put("page", page);
        data.put("size", size);
        data.put("hasMore", page * size < pageInfo.getTotal());
        return R.success(null, data);
    }

    @Override
    public R addSong(SongRequest addSongRequest, MultipartFile mpfile) {
        Song song = new Song();
        BeanUtils.copyProperties(addSongRequest, song);
        String pic = "/img/songPic/tubiao.jpg";
        String objectKey = minioUploadService.uploadSongFile(mpfile);
        if (objectKey == null) {
            return R.fatal("上传失败：文件未写入存储（请确认 MinIO 已启动、可访问 minio.endpoint，且已在日志中自动创建 bucket）");
        }
        String storeUrlPath = "/" + bucketName + "/" + objectKey;
        song.setCreateTime(new Date());
        song.setUpdateTime(new Date());
        song.setPic(pic);
        song.setUrl(storeUrlPath);
        song.setDuration(sanitizeDuration(song.getDuration()));
        if (songMapper.insert(song) > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("url", storeUrlPath);
            return R.success("上传成功", data);
        } else {
            return R.error("上传失败");
        }
    }

    @Override
    public R updateSongMsg(SongRequest updateSongRequest) {
        Song song = new Song();
        BeanUtils.copyProperties(updateSongRequest, song);
        if (songMapper.updateById(song) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updateSongUrl(MultipartFile urlFile, int id) {
        Song existing = songMapper.selectById(id);
        if (existing != null && existing.getUrl() != null) {
            String prefix = "/" + bucketName + "/";
            if (existing.getUrl().startsWith(prefix)) {
                String oldObject = existing.getUrl().substring(prefix.length());
                if (!oldObject.contains("/")) {
                    try {
                        minioClient.removeObject(
                                RemoveObjectArgs.builder().bucket(bucketName).object(oldObject).build());
                    } catch (Exception e) {
                        log.warn("MinIO removeObject skipped for {}: {}", oldObject, e.getMessage());
                    }
                }
            }
        }
        String objectKey = minioUploadService.uploadSongFile(urlFile);
        if (objectKey == null) {
            return R.fatal("更新失败：文件未写入存储，请检查 MinIO");
        }
        String storeUrlPath = "/" + bucketName + "/" + objectKey;
        Song song = new Song();
        song.setId(id);
        song.setUrl(storeUrlPath);
        if (songMapper.updateById(song) > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("url", storeUrlPath);
            return R.success("更新成功", data);
        } else {
            return R.error("更新失败");
        }
    }

    @Override
    public R updateSongPic(MultipartFile urlFile, int id) {
        String fileName = System.currentTimeMillis() + urlFile.getOriginalFilename();
        if (!minioUploadService.uploadSongCoverImg(urlFile, fileName)) {
            return R.fatal("上传失败");
        }
        String storeUrlPath = "/" + bucketName + "/singer/song/" + fileName;
        Song song = new Song();
        song.setId(id);
        song.setPic(storeUrlPath);
        if (songMapper.updateById(song) > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("url", storeUrlPath);
            return R.success("上传成功", data);
        } else {
            return R.error("上传失败");
        }
    }

    @Override
    public R deleteSong(Integer id) {
        Song song = songMapper.selectById(id);
        if (songMapper.deleteById(id) > 0) {
            if (song != null && song.getUrl() != null) {
                String prefix = "/" + bucketName + "/";
                if (song.getUrl().startsWith(prefix)) {
                    String objectName = song.getUrl().substring(prefix.length());
                    if (!objectName.contains("/")) {
                        try {
                            minioClient.removeObject(
                                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
                        } catch (Exception e) {
                            log.warn("MinIO removeObject skipped for {}: {}", objectName, e.getMessage());
                        }
                    }
                }
            }
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R songOfSingerId(Integer singerId) {
        try {
            QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("singer_id", singerId);
            java.util.List<Song> songs = songMapper.selectList(queryWrapper);
            log.info("Query songs by singerId={}, count={}", singerId, songs != null ? songs.size() : 0);
            if (songs != null && !songs.isEmpty()) {
                songSingerPicSupport.enrichSongs(songs);
                return R.success("查询成功", songs);
            } else {
                return R.success("查询成功，但该歌手暂无歌曲", java.util.Collections.emptyList());
            }
        } catch (Exception e) {
            log.error("Failed to query songs by singerId={}", singerId, e);
            return R.fatal("查询失败: " + e.getMessage());
        }
    }

    @Override
    public R songOfId(Integer id) {
        Song song = songMapper.selectById(id);
        if (song != null) {
            songSingerPicSupport.enrichSong(song);
        }
        return R.success(null, song);
    }

    @Override
    public R songOfSingerName(String name) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name",name);
        List<Song> list = songMapper.selectList(queryWrapper);
        songSingerPicSupport.enrichSongs(list);
        return R.success(null, list);
    }

    @Override
    public R updateSongLyric(Integer id, String lyric) {
        if (id == null || id <= 0) {
            return R.error("id非法");
        }
        String text = lyric == null ? "" : lyric;
        LambdaUpdateWrapper<Song> uw = new LambdaUpdateWrapper<>();
        uw.eq(Song::getId, id).set(Song::getLyric, text).set(Song::getUpdateTime, new Date());
        boolean ok = update(uw);
        return ok ? R.success("保存成功") : R.error("保存失败");
    }

    @Override
    public R getSongCollectRank() {
        String cacheKey = "song:rank:collect";

        String cache = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cache)) {
            List<Map<String, Object>> rankList =
                    JSON.parseObject(cache, new TypeReference<List<Map<String, Object>>>() {});
            songSingerPicSupport.enrichRankSongCovers(rankList);
            return R.success("排行榜(缓存)", rankList);
        }

        List<Map<String, Object>> rankList = songMapper.selectSongRankByCollectCount();
        if (rankList == null) {
            rankList = Collections.emptyList();
        }
        songSingerPicSupport.enrichRankSongCovers(rankList);

        String json = JSON.toJSONString(rankList);
        stringRedisTemplate.opsForValue().set(cacheKey, json, 10, TimeUnit.MINUTES);

        return R.success("排行榜查询成功", rankList);
    }

    private Integer sanitizeDuration(Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds < 0) {
            return 0;
        }
        return durationSeconds;
    }

}
