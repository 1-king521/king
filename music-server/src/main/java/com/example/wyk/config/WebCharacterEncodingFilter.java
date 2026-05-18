package com.example.wyk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 说明：解决以下问题
 * <p>
 *     因使用WebMvcConfigurer加载静态时 url encode编码无法解析为正常中文问题
 * </p>
 * 注意：移除了 @EnableWebMvc 以保持Spring Boot的自动静态资源处理
 */

@Configuration
public class WebCharacterEncodingFilter implements WebMvcConfigurer {

    /**
     * 乱码处理
     */
    public HttpMessageConverter<String> responseBodyConverter() {
        final StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converter.setWriteAcceptCharset(false);
        return converter;
    }

    /**
     * CORS 由 {@link WebMvcConfig} 统一配置。此处若再注册 allowedOriginPatterns("*") + allowCredentials(true)，
     * 浏览器会拒绝预检，前端 Axios 表现为 Network Error。
     */

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        if (!converters.isEmpty()) {
            converters.add(converters.get(0));
            converters.set(0, responseBodyConverter());
        } else {
            converters.add(responseBodyConverter());
        }
    }
}

