package com.example.wyk.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("user_ai_chat")
public class UserAiChat {
        @TableId(type = IdType.AUTO)
        private Integer id;
        private Integer userId;
        private String sessionId;
        private String userMsg;
        private String aiMsg;
        private LocalDateTime createTime;
}
