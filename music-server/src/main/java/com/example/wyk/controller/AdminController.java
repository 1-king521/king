package com.example.wyk.controller;

import com.example.wyk.common.R;
import com.example.wyk.model.request.AdminRequest;
import com.example.wyk.security.LoginAttemptService;
import com.example.wyk.service.AdminService;
import com.example.wyk.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理的相关事宜
 */
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    private final LoginAttemptService loginAttemptService;

    // 判断是否登录成功
    @PostMapping("/admin/login/status")
    public R loginStatus(@RequestBody AdminRequest adminRequest, HttpSession session, HttpServletRequest request) {
        String account = adminRequest.getUsername();
        if (loginAttemptService.isBlocked(account, request)) {
            long remain = loginAttemptService.getRemainingSeconds(account, request);
            return R.error("登录失败次数过多，请" + remain + "秒后重试");
        }

        R result = adminService.verityPasswd(adminRequest, session);
        if (Boolean.TRUE.equals(result.getSuccess())) {
            loginAttemptService.onLoginSuccess(account, request);
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", account);
            claims.put("role", "admin");
            String jwt = JwtUtil.generateToken(claims);
            return R.success("登录成功", jwt);
        }
        loginAttemptService.onLoginFailure(account, request);
        return result;
    }

    /**
     * 管理端顶部头像；与前台用户头像同存 MinIO 路径前缀，库表无单独字段。
     */
    @PostMapping("/admin/avatar/upload")
    public R uploadAdminAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return adminService.uploadAdminAvatar(file, request);
    }
}
