package com.example.wyk.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_play_history")
public class UserPlayHistory {
    // 主键自增
    @TableId(type = IdType.AUTO)
    private Integer id;

    // 用户ID（外键关联 user 表）
    private Integer userId;

    // 歌曲ID（外键关联 song 表）
    private Integer songId;

    // 播放时间
    private Date playTime;
}
