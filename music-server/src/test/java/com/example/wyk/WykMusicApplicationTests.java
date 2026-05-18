package com.example.wyk;

import com.example.wyk.service.impl.AppUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WykMusicApplicationTests {

    @Autowired
    private AppUserServiceImpl appUserService;

    @Test
    public void appUserListSmokeTest() {
        System.out.println(appUserService.allUser());
    }
}
