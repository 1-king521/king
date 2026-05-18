package com.example.wyk.controller;

import com.example.wyk.common.R;
import com.example.wyk.model.request.ListSongRequest;
import com.example.wyk.service.ListSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ListSongController {

    private final ListSongService listSongService;


    // 给歌单添加歌曲
    @PostMapping("/listSong/add")
    public R addListSong(@RequestBody ListSongRequest addListSongRequest) {
        return listSongService.addListSong(addListSongRequest);
    }

    @GetMapping("/listSong/delete")
    public R deleteListSong(@RequestParam int songListId, @RequestParam int songId) {
        return listSongService.deleteListSong(songListId, songId);
    }

    // 返回歌单里指定歌单 ID 的歌曲
    @GetMapping("/listSong/detail")
    public R listSongOfSongId(@RequestParam int songListId) {
        return listSongService.listSongOfSongId(songListId);
    }

    // 返回歌单里指定歌单 ID 的歌曲详情
    @GetMapping("/listSong/song/detail")
    public R songDetailOfSongListId(@RequestParam int songListId) {
        return listSongService.songDetailOfSongListId(songListId);
    }

    // 更新歌单里面的歌曲信息
    @PostMapping("/listSong/update")
    public R updateListSongMsg(@RequestBody ListSongRequest updateListSongRequest) {
        return listSongService.updateListSongMsg(updateListSongRequest);
    }
}
