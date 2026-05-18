package com.example.wyk.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    // 密钥（必须16位以上，自己随便改）
    private static final String SECRET = "wykAndKing";
    private static final long EXPIRATION = 1000 * 60 * 60 ; // 1h


    // 生成 token
    public static String generateToken(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        return jwt;
    }

    // 解析 token
    public static Claims parseToken(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}
