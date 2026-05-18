package com.example.wyk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wyk.model.domain.AppUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {
}
