package com.example.wyk.controller;

import com.example.wyk.common.R;
import com.example.wyk.model.domain.UserAiChat;
import com.example.wyk.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/music/ai")
@RequiredArgsConstructor
public class AiChatController {
    private final AiChatService aiChatService;

    /**
     * 流式AI聊天接口（核心接口）
     */
    @GetMapping("/stream/chat")
    public SseEmitter streamChat(@RequestParam Integer userId,
                                 @RequestParam String message){
        return aiChatService.streamAiChat(userId,message);
    }

    /**
     * 查询AI聊天历史
     */
    @GetMapping("/history/{userId}")
    public R getAiChatHistory(@PathVariable Integer userId){
        List<UserAiChat> list = aiChatService.getUserChatHistory(userId);
        return R.success("查询成功", list);
    }
}
