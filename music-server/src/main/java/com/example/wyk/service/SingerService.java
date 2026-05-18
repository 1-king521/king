package com.example.wyk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wyk.common.R;
import com.example.wyk.model.domain.Singer;
import com.example.wyk.model.request.SingerRequest;
import org.springframework.web.multipart.MultipartFile;

public interface SingerService extends IService<Singer> {

    R addSinger (SingerRequest addSingerRequest);

    R updateSingerMsg(SingerRequest updateSingerRequest);

    R updateSingerPic(MultipartFile avatorFile, int id);

    R deleteSinger(Integer id);

    R allSinger();

    R pageSinger(Integer page, Integer size);

    R singerOfName(String name);

    R singerOfSex(Integer sex);

    R allSingerStyle();
}
