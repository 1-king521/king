package com.example.wyk.config;

import com.example.wyk.constant.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 集中一下图像的配置类吧
 *
 * @Author 祝英台炸油条
 * @Time : 2022/6/5 22:23
 **/
@Configuration
public class WebPicConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/avatorImages/**")
                .addResourceLocations(Constants.AVATAR_IMAGES_PATH)
                .setCachePeriod(3600);
        registry.addResourceHandler("/img/singerPic/**")
                .addResourceLocations(Constants.SINGER_PIC_PATH)
                .setCachePeriod(3600);
        /* 优先本地 user.dir/img/songPic，其次 classpath（内置默认 tubiao 等） */
        registry.addResourceHandler("/img/songPic/**")
                .addResourceLocations(Constants.SONG_PIC_PATH, "classpath:/static/img/songPic/")
                .setCachePeriod(3600);
        registry.addResourceHandler("/song/**")
                .addResourceLocations(Constants.SONG_PATH)
                .setCachePeriod(3600);
        registry.addResourceHandler("/img/songListPic/**")
                .addResourceLocations(Constants.SONGLIST_PIC_PATH)
                .setCachePeriod(3600);
        registry.addResourceHandler("/img/swiper/**")
                .addResourceLocations(Constants.BANNER_PIC_PATH)
                .setCachePeriod(3600);
    }

}
