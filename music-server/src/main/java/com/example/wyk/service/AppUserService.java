package com.example.wyk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wyk.common.R;
import com.example.wyk.model.domain.AppUser;
import com.example.wyk.model.request.AppUserRequest;
import com.example.wyk.model.request.CodeLoginRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface AppUserService extends IService<AppUser> {

    R addUser(AppUserRequest registryRequest);

    R updateUserMsg(AppUserRequest updateRequest);

    R updateUserAvatar(MultipartFile avatarFile, int id);

    R updatePassword(AppUserRequest updatePasswordRequest);

    boolean existUser(String username);

    boolean verityPasswd(String username, String password);

    R deleteUser(Integer id);

    R allUser();

    R pageUser(Integer page, Integer size);

    R userOfId(Integer id);

    /** 当前登录用户（根据请求头 token 解析），用于前台登录后拉取资料，避免调用需管理员权限的 {@code GET /user/page} */
    R currentUserProfile(HttpServletRequest request);

    R loginStatus(AppUserRequest loginRequest);

    R sendCode(String phone);

    R CodeLogin(CodeLoginRequest req);
}
