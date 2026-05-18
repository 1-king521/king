package com.example.wyk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wyk.common.R;
import com.example.wyk.model.domain.SongList;
import com.example.wyk.model.request.SongListRequest;
import org.springframework.web.multipart.MultipartFile;

public interface SongListService extends IService<SongList> {

    R addSongList(SongListRequest addSongListRequest);

    R updateSongListMsg(SongListRequest updateSongListRequest);

    R updateSongListImg(MultipartFile avatorFile, int id);

    R deleteSongList(Integer id);

    R allSongList();

    R pageSongList(Integer page, Integer size);

    R songListOfId(Integer id);

    R likeTitle(String title);

    R likeStyle(String style);

    R pageLikeStyle(String style, Integer page, Integer size);

    R allSongListStyle();

    R recommendSongList(Integer limit);

    /** 歌单收藏数排行榜（Redis 缓存 10 分钟） */
    R getSongListCollectRank();
}
