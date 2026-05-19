package com.example.wyk.service;

import com.example.wyk.model.domain.UserAiChat;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface AiChatService {
    // 流式AI对话
    SseEmitter streamAiChat(Integer userId, String content);
    // 保存聊天记录
    void saveChatRecord(Integer userId,String sessionId,String userMsg,String aiMsg);
    // 查询历史对话
    List<UserAiChat> getUserChatHistory(Integer userId);
}
