package com.example.wyk.controller;

import com.example.wyk.common.R;
import com.example.wyk.service.LrclibLyricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代理拉取外部同步歌词（LRCLIB），供前端在占位歌词时自动补全。
 */
@RequiredArgsConstructor
@RestController
public class LyricsFetchController {

    private final LrclibLyricsService lrclibLyricsService;

    @GetMapping("/lyrics/fetch")
    public R fetchLyrics(
            @RequestParam("trackName") String trackName,
            @RequestParam(value = "artistName", required = false, defaultValue = "") String artistName,
            @RequestParam(value = "albumName", required = false, defaultValue = "") String albumName,
            @RequestParam(value = "duration", required = false) Integer duration) {
        String lyrics = lrclibLyricsService.fetchSyncedLyrics(trackName, artistName, albumName, duration);
        if (lyrics == null || lyrics.isEmpty()) {
            return R.error("未在歌词库中找到匹配歌词，可尝试核对歌名、歌手或时长");
        }
        return R.success("获取成功", lyrics);
    }
}
