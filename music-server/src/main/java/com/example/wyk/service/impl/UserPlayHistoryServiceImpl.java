package com.example.wyk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wyk.common.R;
import com.example.wyk.mapper.UserPlayHistoryMapper;
import com.example.wyk.model.domain.UserPlayHistory;
import com.example.wyk.service.UserPlayHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserPlayHistoryServiceImpl extends ServiceImpl<UserPlayHistoryMapper, UserPlayHistory>
        implements UserPlayHistoryService {
    private static final String PLAY_HISTORY_CACHE_KEY = "user:playHistory:";
    private final UserPlayHistoryMapper userPlayHistoryMapper;
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public R addPlayHistory(Integer userId, Integer songId) {
        // 1. 插入播放记录
        UserPlayHistory history = new UserPlayHistory();
        history.setUserId(userId);
        history.setSongId(songId);
        history.setPlayTime(new Date());
        userPlayHistoryMapper.insert(history);

        // 2. 删除缓存，让下次查询自动更新
        stringRedisTemplate.delete(PLAY_HISTORY_CACHE_KEY + userId);

        return R.success("播放记录已保存");
    }

    @Override
    public R getPlayHistory(Integer userId) {
        String cacheKey = PLAY_HISTORY_CACHE_KEY + userId;
        String cache = stringRedisTemplate.opsForValue().get(cacheKey);

        // 1. 缓存命中，直接返回
        if (StringUtils.hasText(cache)) {
            List<Map<String, Object>> list = JSON.parseObject(cache, new TypeReference<List<Map<String, Object>>>() {});
            return R.success("播放历史(缓存)", list);
        }

        // 2. 缓存未命中，查询数据库
        List<Map<String, Object>> list = userPlayHistoryMapper.selectUserPlayHistory(userId);

        // 3. 写入缓存（10分钟过期）
        stringRedisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(list), 10, TimeUnit.MINUTES);

        return R.success("播放历史", list);
    }
}
