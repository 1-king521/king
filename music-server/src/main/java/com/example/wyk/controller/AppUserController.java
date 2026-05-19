package com.example.wyk.controller;

import com.example.wyk.common.R;
import com.example.wyk.model.request.AppUserRequest;
import com.example.wyk.model.request.CodeLoginRequest;
import com.example.wyk.model.request.PasswordResetRequest;
import com.example.wyk.security.LoginAttemptService;
import com.example.wyk.service.AppUserService;
import com.example.wyk.service.PasswordResetService;
import com.example.wyk.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AppUserController {

    private final AppUserService appUserService;

    private final LoginAttemptService loginAttemptService;

    private final PasswordResetService passwordResetService;

    @PostMapping("/user/add")
    public R addUser(@RequestBody AppUserRequest registryRequest) {
        return appUserService.addUser(registryRequest);
    }

    @GetMapping("/user/sendVerificationCode")
    public R sendVerificationCode(@RequestParam String email) {
        return passwordResetService.sendVerificationCode(email);
    }

    @PostMapping("/user/resetPassword")
    public R resetPassword(@RequestBody PasswordResetRequest request) {
        return passwordResetService.resetPassword(request);
    }

    @PostMapping("/user/login/status")
    public R loginStatus(@RequestBody AppUserRequest loginRequest,  HttpServletRequest request) {
        String account = loginRequest.getUsername();
        if (loginAttemptService.isBlocked(account, request)) {
            long remain = loginAttemptService.getRemainingSeconds(account, request);
            return R.error("登录失败次数过多，请" + remain + "秒后重试");
        }

        R result = appUserService.loginStatus(loginRequest);
        if (Boolean.TRUE.equals(result.getSuccess())) {
            loginAttemptService.onLoginSuccess(account, request);
            //生成jwt
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", loginRequest.getUsername());
            claims.put("password", loginRequest.getPassword());
            String jwt = JwtUtil.generateToken(claims);
            return R.success("登录成功", jwt);
        } else {
            loginAttemptService.onLoginFailure(account, request);
        }
        return result;
    }

    @GetMapping("/user")
    public R allUser() {
        return appUserService.allUser();
    }

    @GetMapping("/user/page")
    public R pageUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        return appUserService.pageUser(safePage, safeSize);
    }

    @GetMapping("/user/detail")
    public R userOfId(@RequestParam int id) {
        return appUserService.userOfId(id);
    }

    /** 前台登录后拉取当前用户（普通用户 JWT 即可），勿用 {@code GET /user/page}（管理端专用、需 admin 角色） */
    @GetMapping("/user/profile")
    public R currentUserProfile(HttpServletRequest request) {
        return appUserService.currentUserProfile(request);
    }

    @GetMapping("/user/delete")
    public R deleteUser(@RequestParam int id) {
        return appUserService.deleteUser(id);
    }

    @PostMapping("/user/update")
    public R updateUserMsg(@RequestBody AppUserRequest updateRequest) {
        return appUserService.updateUserMsg(updateRequest);
    }

    @PostMapping("/user/updatePassword")
    public R updatePassword(@RequestBody AppUserRequest updatePasswordRequest) {
        return appUserService.updatePassword(updatePasswordRequest);
    }

    @PostMapping("/user/avatar/update")
    public R updateUserPic(@RequestParam("file") MultipartFile avatarFile, @RequestParam("id") int id) {
        return appUserService.updateUserAvatar(avatarFile, id);
    }
    @PostMapping("/user/login/sendCode")
    public R sendCode(@RequestParam String phone){
        return appUserService.sendCode(phone);
    }
    @PostMapping("/login")
    public R smsLogin(@RequestBody CodeLoginRequest req) {
        return appUserService.CodeLogin(req);
    }
}
