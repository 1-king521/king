package com.example.wyk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wyk.model.domain.UserPlayHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserPlayHistoryMapper extends BaseMapper<UserPlayHistory> {

    List<Map<String, Object>> selectUserPlayHistory(@Param("userId") Integer userId);
}
