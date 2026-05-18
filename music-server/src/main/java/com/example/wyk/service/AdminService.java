package com.example.wyk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wyk.common.R;
import com.example.wyk.model.domain.Admin;
import com.example.wyk.model.request.AdminRequest;

import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface AdminService extends IService<Admin> {

    R verityPasswd(AdminRequest adminRequest, HttpSession session);

    /** 管理员头像上传到 MinIO；鉴权在实现内校验 Session 或 admin JWT */
    R uploadAdminAvatar(MultipartFile file, HttpServletRequest request);
}
