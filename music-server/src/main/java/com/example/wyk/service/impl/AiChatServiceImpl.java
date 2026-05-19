package com.example.wyk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.wyk.config.MusicAiPromptConfig;
import com.example.wyk.mapper.UserAiChatMapper;
import com.example.wyk.mapper.UserPlayHistoryMapper;
import com.example.wyk.model.domain.UserAiChat;
import com.example.wyk.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient chatClient;
    private final UserAiChatMapper chatMapper;
    private final UserPlayHistoryMapper historyMapper;

    public AiChatServiceImpl(ChatModel chatModel,
                             UserAiChatMapper chatMapper,
                             UserPlayHistoryMapper historyMapper) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.chatMapper = chatMapper;
        this.historyMapper = historyMapper;
    }

    @Override
    public SseEmitter streamAiChat(Integer userId, String content) {
        // 0 = 不超时，避免 DeepSeek 流式较慢时被切断
        SseEmitter emitter = new SseEmitter(0L);
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        String userPref = getUserMusicPref(userId);
        StringBuilder fullAiReply = new StringBuilder();

        chatClient.prompt()
                .system(MusicAiPromptConfig.MUSIC_AGENT_ROLE)
                .user(userPref + "\n用户问题：" + content)
                .stream()
                .content()
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                        chunk -> {
                            fullAiReply.append(chunk);
                            try {
                                synchronized (emitter) {
                                    emitter.send(SseEmitter.event().data(chunk));
                                }
                            } catch (IOException e) {
                                log.warn("SSE send failed: {}", e.toString());
                                try {
                                    emitter.completeWithError(e);
                                } catch (Exception ignored) {
                                    /* ignore */
                                }
                            }
                        },
                        err -> handleStreamUpstreamError(err, emitter),
                        () -> {
                            try {
                                saveChatRecord(userId, sessionId, content, fullAiReply.toString());
                            } catch (Exception e) {
                                log.warn("saveChatRecord failed: {}", e.toString());
                            }
                            try {
                                emitter.complete();
                            } catch (IllegalStateException ex) {
                                log.debug("SSE emitter already completed");
                            }
                        }
                );

        return emitter;
    }

    private String getUserMusicPref(Integer userId) {
        List<Map<String, Object>> historyList = historyMapper.selectUserPlayHistory(userId);
        if (historyList.isEmpty()) {
            return "用户暂无听歌记录，按照大众喜好推荐音乐";
        }
        StringBuilder sb = new StringBuilder("用户常听歌曲：");
        historyList.forEach(item -> {
            Object songName = item.get("songName");
            if (songName != null && !songName.toString().isBlank()) {
                sb.append(songName).append(' ');
            }
        });
        return sb.toString();
    }

    @Override
    public void saveChatRecord(Integer userId, String sessionId, String userMsg, String aiMsg) {
        UserAiChat chat = new UserAiChat();
        chat.setUserId(userId);
        chat.setSessionId(sessionId);
        chat.setUserMsg(userMsg);
        chat.setAiMsg(aiMsg != null ? aiMsg : "");
        chat.setCreateTime(LocalDateTime.now());
        chatMapper.insert(chat);
    }

    @Override
    public List<UserAiChat> getUserChatHistory(Integer userId) {
        return chatMapper.selectList(new LambdaQueryWrapper<UserAiChat>()
                .eq(UserAiChat::getUserId, userId)
                .orderByDesc(UserAiChat::getCreateTime));
    }

    /**
     * 上游大模型接口返回 HTTP 错误时：向前端 SSE 写入可读说明（如 DeepSeek 402 额度不足）。
     */
    private static void handleStreamUpstreamError(Throwable err, SseEmitter emitter) {
        log.error("AI stream error", err);
        String tip = upstreamTip(err);
        try {
            synchronized (emitter) {
                emitter.send(SseEmitter.event().data(tip));
            }
            emitter.complete();
        } catch (Exception sendEx) {
            log.warn("SSE error notify failed: {}", sendEx.toString());
            try {
                emitter.completeWithError(err);
            } catch (Exception ignored) {
                /* ignore */
            }
        }
    }

    /** 402=计费/配额；401=密钥；429=限速；其余泛化提示 */
    private static String upstreamTip(Throwable err) {
        WebClientResponseException wcex = findWebClientResponseException(err);
        if (wcex == null) {
            return "AI 服务请求失败：" + err.getMessage();
        }
        int code = wcex.getStatusCode().value();
        return switch (code) {
            case 401 -> "大模型接口鉴权失败（401），请检查 API Key 是否与账户匹配。";
            case 402 -> "大模型账户余额不足或需开通计费（402），请到 DeepSeek 控制台充值/查看额度后再试。";
            case 429 -> "大模型接口请求过于频繁（429），请稍后再试。";
            default -> "大模型接口错误（HTTP " + code + "），请稍后重试或查阅服务商说明。";
        };
    }

    private static WebClientResponseException findWebClientResponseException(Throwable err) {
        for (Throwable t = err; t != null; t = t.getCause()) {
            if (t instanceof WebClientResponseException w) {
                return w;
            }
        }
        return null;
    }
}
