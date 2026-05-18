package com.example.wyk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wyk.common.R;
import com.example.wyk.mapper.AppUserMapper;
import com.example.wyk.model.domain.AppUser;
import com.example.wyk.model.request.AppUserRequest;
import com.example.wyk.model.request.CodeLoginRequest;
import com.example.wyk.service.AppUserService;
import com.example.wyk.service.MinioUploadService;
import com.example.wyk.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.example.wyk.constant.Constants.SALT;

@RequiredArgsConstructor
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser>
        implements AppUserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String DEFAULT_AVATAR = "/img/avatorImages/user.jpg";

    private final AppUserMapper appUserMapper;

    private final PasswordEncoder passwordEncoder;

    private final MinioUploadService minioUploadService;

    @Override
    public R addUser(AppUserRequest registryRequest) {
        if (this.existUser(registryRequest.getUsername())) {
            return R.warning("用户名已注册");
        }
        AppUser appUser = new AppUser();
        BeanUtils.copyProperties(registryRequest, appUser);
        String password = passwordEncoder.encode(registryRequest.getPassword());
        appUser.setPassword(password);
        if (StringUtils.isBlank(appUser.getPhoneNum())) {
            appUser.setPhoneNum(null);
        }
        if ("".equals(appUser.getEmail())) {
            appUser.setEmail(null);
        }
        if (StringUtils.isBlank(appUser.getAvatar())) {
            appUser.setAvatar(DEFAULT_AVATAR);
        } else if (!appUser.getAvatar().startsWith("/")) {
            appUser.setAvatar("/" + appUser.getAvatar());
        }
        try {
            if (appUserMapper.insert(appUser) > 0) {
                return R.success("注册成功");
            } else {
                return R.error("注册失败");
            }
        } catch (DuplicateKeyException e) {
            return R.fatal(e.getMessage());
        }
    }

    @Override
    public R updateUserMsg(AppUserRequest updateRequest) {
        AppUser appUser = new AppUser();
        BeanUtils.copyProperties(updateRequest, appUser);
        if (appUserMapper.updateById(appUser) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updatePassword(AppUserRequest updatePasswordRequest) {

        if (!this.verityPasswd(updatePasswordRequest.getUsername(), updatePasswordRequest.getOldPassword())) {
            return R.error("密码输入错误");
        }

        AppUser appUser = new AppUser();
        appUser.setId(updatePasswordRequest.getId());
        appUser.setPassword(passwordEncoder.encode(updatePasswordRequest.getPassword()));

        if (appUserMapper.updateById(appUser) > 0) {
            return R.success("密码修改成功");
        } else {
            return R.error("密码修改失败");
        }
    }

    @Override
    public R updateUserAvatar(MultipartFile avatarFile, int id) {
        String fileName = System.currentTimeMillis() + avatarFile.getOriginalFilename();
        if (!minioUploadService.uploadAvatarImg(avatarFile, fileName)) {
            return R.fatal("上传失败");
        }
        String imgPath = "/img/avatorImages/" + fileName;
        AppUser appUser = new AppUser();
        appUser.setId(id);
        appUser.setAvatar(imgPath);
        if (appUserMapper.updateById(appUser) > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("url", imgPath);
            return R.success("上传成功", data);
        } else {
            return R.error("上传失败");
        }
    }

    @Override
    public boolean existUser(String username) {
        QueryWrapper<AppUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return appUserMapper.selectCount(queryWrapper) > 0;
    }

    private AppUser findAppUserByLoginAccount(String account) {
        if (StringUtils.isBlank(account)) {
            return null;
        }
        String trimmed = account.trim();
        QueryWrapper<AppUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(w -> w.eq("username", trimmed).or().eq("email", trimmed));
        return appUserMapper.selectOne(queryWrapper);
    }

    private boolean matchesStoredPassword(AppUser appUser, String password) {
        if (appUser == null || StringUtils.isBlank(appUser.getPassword())) {
            return false;
        }
        String storedPassword = appUser.getPassword();
        if (passwordEncoder.matches(password, storedPassword)) {
            return true;
        }

        String legacyPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
        if (legacyPassword.equals(storedPassword)) {
            AppUser update = new AppUser();
            update.setId(appUser.getId());
            update.setPassword(passwordEncoder.encode(password));
            appUserMapper.updateById(update);
            return true;
        }
        return false;
    }

    @Override
    public boolean verityPasswd(String username, String password) {
        AppUser appUser = findAppUserByLoginAccount(username);
        return matchesStoredPassword(appUser, password);
    }

    @Override
    public R deleteUser(Integer id) {
        if (appUserMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R allUser() {
        return R.success(null, appUserMapper.selectList(null));
    }

    @Override
    public R pageUser(Integer page, Integer size) {
        Page<AppUser> pageInfo = new Page<>(page, size);
        appUserMapper.selectPage(pageInfo, null);
        Map<String, Object> data = new HashMap<>();
        data.put("records", pageInfo.getRecords());
        data.put("total", pageInfo.getTotal());
        data.put("page", page);
        data.put("size", size);
        data.put("hasMore", page * size < pageInfo.getTotal());
        return R.success(null, data);
    }

    @Override
    public R userOfId(Integer id) {
        AppUser appUser = appUserMapper.selectById(id);
        return R.success(null, appUser);
    }

    @Override
    public R currentUserProfile(HttpServletRequest request) {
        String jwt = request.getHeader("token");
        if (StringUtils.isBlank(jwt)) {
            return R.error("未登录");
        }
        try {
            Claims claims = JwtUtil.parseToken(jwt);
            Object claimAccount = claims.get("username");
            if (claimAccount == null) {
                return R.error("未登录");
            }
            String account = claimAccount.toString().trim();
            AppUser appUser = findAppUserByLoginAccount(account);
            if (appUser == null) {
                return R.error("用户不存在");
            }
            appUser.setPassword(null);
            return R.success(null, appUser);
        } catch (Exception e) {
            return R.error("未登录");
        }
    }

    @Override
    public R loginStatus(AppUserRequest loginRequest) {

        String account = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (!this.verityPasswd(account, password)) {
            return R.error("用户名或密码错误");
        }
        AppUser appUser = findAppUserByLoginAccount(account);
        return R.success("登录成功", appUser);
    }

    @Override
    public R sendCode(String phone) {
        //1.校验手机号
        // 与注册侧一致：只校验 11 位、以 1 开头（测试号如 12345678977 也能发码）
        if (!phone.matches("^1\\d{10}$")) {
            return R.warning("请输入正确的手机号");
        }
        //2.手机号正确，生成验证码
        // 生成6位数字验证码 000000~999999
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        //3.保存验证码到redis中
        String codeKey = "code:" + phone;
        stringRedisTemplate.opsForValue().set(codeKey, code, 1, TimeUnit.MINUTES);
        //4.发送验证码
        return R.success("验证码已发送");
    }

    @Override
    public R CodeLogin(CodeLoginRequest req) {
        String phone = req.getPhone();
        String inputCode = req.getCode();
        // 1. 从Redis取真实验证码
        String codeKey = "code:" + phone;
        String realCode = stringRedisTemplate.opsForValue().get(codeKey);

        // 2. 校验验证码
        if (StringUtils.isBlank(realCode) || !realCode.equals(inputCode)) {
            return R.warning("验证码错误或已过期");
        }

        // 3. 根据手机号查用户
        // 根据手机号查询用户（MyBatis-Plus）
        AppUser user = lambdaQuery()
                .eq(AppUser::getPhoneNum, phone)
                .one();
        // 可选：手机号没注册 可以自动注册新用户（我后面给你补）
        if (user == null) {
            return R.warning("该手机号未注册，请先注册");
        }
        // 4. 校验通过 → 生成 JWT（复用你现有的JwtUtil）
        Map<String, Object> claims = new HashMap<>();
        claims.put("username",user.getUsername() );
        claims.put("password", user.getPhoneNum());
        String jwt = JwtUtil.generateToken(claims);
        // 登录成功删除验证码
        stringRedisTemplate.delete(codeKey);
        return R.success("登录成功",jwt);
    }
}
