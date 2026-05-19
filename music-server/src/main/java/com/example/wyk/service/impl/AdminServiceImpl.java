package com.example.wyk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wyk.common.R;
import com.example.wyk.mapper.AdminMapper;
import com.example.wyk.model.domain.Admin;
import com.example.wyk.model.request.AdminRequest;
import com.example.wyk.service.AdminService;
import com.example.wyk.service.MinioUploadService;
import com.example.wyk.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final AdminMapper adminMapper;

    private final PasswordEncoder passwordEncoder;

    private final MinioUploadService minioUploadService;

    @Override
    public R verityPasswd(AdminRequest adminRequest, HttpSession session) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",adminRequest.getUsername());
        Admin admin = adminMapper.selectOne(queryWrapper);
        if (admin == null || StringUtils.isBlank(admin.getPassword())) {
            return R.error("用户名或密码错误");
        }

        String rawPassword = adminRequest.getPassword();
        String storedPassword = admin.getPassword();
        boolean pass = passwordEncoder.matches(rawPassword, storedPassword) || rawPassword.equals(storedPassword);
        if (pass) {
            if (rawPassword.equals(storedPassword)) {
                Admin update = new Admin();
                update.setId(admin.getId());
                update.setPassword(passwordEncoder.encode(rawPassword));
                adminMapper.updateById(update);
            }
            session.setAttribute("name", adminRequest.getUsername());
            return R.success("登录成功");
        }
        return R.error("用户名或密码错误");
    }

    @Override
    public R uploadAdminAvatar(MultipartFile file, HttpServletRequest request) {
        if (!isAdminAuthenticated(request)) {
            return R.error("未登录");
        }
        String original = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + (original == null || original.isBlank() ? "avatar.jpg" : original);
        if (!minioUploadService.uploadAvatarImg(file, fileName)) {
            return R.fatal("上传失败");
        }
        String imgPath = "/img/avatorImages/" + fileName;
        Map<String, Object> data = new HashMap<>();
        data.put("url", imgPath);
        return R.success("上传成功", data);
    }

    private static boolean isAdminAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("name") != null) {
            return true;
        }
        String jwt = request.getHeader("token");
        if (StringUtils.isBlank(jwt)) {
            return false;
        }
        try {
            Claims claims = JwtUtil.parseToken(jwt);
            Object role = claims.get("role");
            return role != null && "admin".equals(role.toString());
        } catch (Exception e) {
            return false;
        }
    }
}
