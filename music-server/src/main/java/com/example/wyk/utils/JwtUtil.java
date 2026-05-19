package com.example.wyk.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

/**
 * JWT 签发 / 校验（HS256）。
 * <p>
 * JJWT 0.12 起 HS256 要求密钥长度 ≥256 位，不能再用短字符串字节直接签名。
 * 这里对常量配置串做 SHA-256 延展，得到恰好 256 位的 HMAC 密钥，满足 RFC 7518/JJWT 校验。
 * <p>
 * <b>注意：</b>与早期 Boot2 + jjwt 0.9 使用「明文短密钥字节」签出的 token 不再兼容，
 * 用户需重新登录一次；升级后服务端签发的新 token 彼此仍可正常解析。
 */
public class JwtUtil {

    /** 与历史配置一致的业务口令；仅供派生密钥，不参与直接 HMAC。 */
    private static final String SECRET = "wykAndKing";

    private static final long EXPIRATION = 1000 * 60 * 60; // 1h

    /** 将配置口令延展为 HS256 所需 ≥256bit 密钥。 */
    private static SecretKey signingKey() {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(SECRET.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(digest, "HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    public static Claims parseToken(String jwt) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
