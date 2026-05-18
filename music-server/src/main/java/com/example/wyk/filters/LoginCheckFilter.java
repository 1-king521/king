package com.example.wyk.filters;

import com.alibaba.fastjson.JSONObject;
import com.example.wyk.common.R;
import com.example.wyk.utils.JwtUtil;
import io.jsonwebtoken.Claims;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * JWT 校验。登录相关接口与「游客可浏览」的读接口放行，其余请求须带合法 header {@code token}。
 * <p>
 * 原先仅对 URL 包含 {@code login} 放行，导致未登录用户访问首页/歌单/歌曲等全部被拦截，前后端联调失败。
 */
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
            if (uri.isEmpty()) {
                uri = "/";
            }
        }

        HttpSession adminSession = request.getSession(false);
        if (adminSession != null && adminSession.getAttribute("name") != null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isAnonymousAllowed(request, uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = request.getHeader("token");
        if (jwt == null || jwt.isEmpty()) {
            writeNotLogin(response);
            return;
        }
        try {
            Claims claims = JwtUtil.parseToken(jwt);
            if (requiresAdminJwt(request.getMethod(), uri)) {
                Object role = claims.get("role");
                if (!"admin".equals(role != null ? role.toString() : null)) {
                    writeNotLogin(response);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeNotLogin(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 用户列表等敏感读接口：若仅依赖 JWT（无后台 Session），必须为管理端签发的 token（含 role=admin），避免普通用户持登录 JWT 拉全站用户。
     */
    private static boolean requiresAdminJwt(String method, String uri) {
        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }
        if ("/user".equals(uri)) {
            return true;
        }
        return uri != null && uri.startsWith("/user/page");
    }

    private static void writeNotLogin(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        R error = R.error("未登录");
        response.getWriter().write(JSONObject.toJSONString(error));
    }

    /**
     * 不需要 JWT 的请求：登录/注册/找回密码、轮播图，以及各类「只读浏览」GET（排除带 delete 的地址）。
     */
    private static boolean isAnonymousAllowed(HttpServletRequest request, String uri) {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        if (uri.contains("login")) {
            return true;
        }

        if ("/user/add".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return true;
        }
        if (uri.startsWith("/user/sendVerificationCode")) {
            return true;
        }
        if ("/user/resetPassword".equals(uri) && "POST".equalsIgnoreCase(method)) {
            return true;
        }

        if ("/banner/getAllBanner".equals(uri)) {
            return true;
        }

        /* 服务端代理拉取 LRCLIB 歌词，仅 GET，无需 JWT（<audio> 场景外由前端主动请求） */
        if ("GET".equalsIgnoreCase(method) && uri.startsWith("/lyrics/")) {
            return true;
        }

        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }

        if (uri.contains("/delete")) {
            return false;
        }

        if (uri.startsWith("/songList")) {
            return true;
        }
        if (uri.startsWith("/singer")) {
            return true;
        }
        if (uri.startsWith("/listSong")) {
            return true;
        }
        if (uri.startsWith("/comment/")) {
            return true;
        }
        if (uri.startsWith("/collection/")) {
            return true;
        }
        if (uri.startsWith("/user/detail")) {
            return true;
        }

        if (uri.startsWith("/song-file")) {
            return true;
        }
        if ("/song".equals(uri) || uri.startsWith("/song?")) {
            return true;
        }
        if (uri.startsWith("/song/")) {
            return true;
        }

        if (uri.contains("/img/") || uri.contains("avatorImages")) {
            return true;
        }

        /*
         * MinIO 等媒体：歌曲 url 存为 /{bucket}/xxx.mp3（见 SongServiceImpl、MinioController）。
         * <audio src> 由浏览器直接拉流，无法带 header token，此处必须放行对应 GET。
         */
        if (isMinioPublicGet(uri)) {
            return true;
        }

        return false;
    }

    /**
     * 与 MinioController 映射一致：/{bucket}/file.ext、/{bucket}/singer/...、/{bucket}/songlist/...
     */
    private static boolean isMinioPublicGet(String uri) {
        if (uri == null) {
            return false;
        }
        int q = uri.indexOf('?');
        String path = q >= 0 ? uri.substring(0, q) : uri;
        if (path.matches("^/[^/]+/[^/]+\\.[^/]+$")) {
            return true;
        }
        return path.matches("^/[^/]+/(singer|songlist)(/.*)?$");
    }
}
