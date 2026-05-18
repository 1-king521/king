package com.example.wyk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wyk.common.R;
import com.example.wyk.model.domain.ListSong;
import com.example.wyk.model.request.ListSongRequest;

import java.util.List;

public interface ListSongService extends IService<ListSong> {

    R addListSong(ListSongRequest addListSongRequest);

    /** 从指定歌单移除一首歌曲（按歌单 id + 歌曲 id），并清理歌单缓存 */
    R deleteListSong(Integer songListId, Integer songId);

    R updateListSongMsg(ListSongRequest updateListSongRequest);
    //看看这啥
    List<ListSong> allListSong();
    R listSongOfSongId(Integer songListId);

    R songDetailOfSongListId(Integer songListId);
}
