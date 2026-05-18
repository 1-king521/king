package com.example.wyk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wyk.common.R;
import com.example.wyk.model.domain.Song;
import com.example.wyk.model.request.SongRequest;
import org.springframework.web.multipart.MultipartFile;

public interface SongService extends IService<Song> {

    R addSong (SongRequest addSongRequest,  MultipartFile mpfile);

    R updateSongMsg(SongRequest updateSongRequest);

    R updateSongUrl(MultipartFile urlFile, int id);

    R updateSongPic(MultipartFile urlFile, int id);

    R deleteSong(Integer id);

    R allSong();

    R pageSong(Integer page, Integer size);

    R songOfSingerId(Integer singerId);

    R songOfId(Integer id);

    R songOfSingerName(String name);

    /** 仅更新歌词字段 */
    R updateSongLyric(Integer id, String lyric);

    R getSongCollectRank();
}
