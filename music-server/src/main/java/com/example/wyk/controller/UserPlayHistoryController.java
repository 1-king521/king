package com.example.wyk.controller;

import com.example.wyk.common.R;
import com.example.wyk.service.UserPlayHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/userPlayHistory")
public class UserPlayHistoryController {

    private final UserPlayHistoryService userPlayHistoryService;

    /**
     * 添加播放记录（用户播放歌曲时调用）
     */
    @PostMapping("/add")
    public R addPlayHistory(@RequestParam Integer userId, @RequestParam Integer songId) {
        return userPlayHistoryService.addPlayHistory(userId, songId);
    }

    /**
     * 查询用户播放历史（用户查看最近播放时调用）
     */
    @GetMapping("/user/{userId}")
    public R getPlayHistory(@PathVariable Integer userId) {
        return userPlayHistoryService.getPlayHistory(userId);
    }
}
