package com.example.wyk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wyk.common.R;
import com.example.wyk.model.domain.UserPlayHistory;

public interface UserPlayHistoryService extends IService<UserPlayHistory> {

    R addPlayHistory(Integer userId, Integer songId);

    R getPlayHistory(Integer userId);
}
