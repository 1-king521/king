package com.example.wyk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
@MapperScan("com.example.wyk.mapper")
public class WykMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(WykMusicApplication.class, args);
    }

}

