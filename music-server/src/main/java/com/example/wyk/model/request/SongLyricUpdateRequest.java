package com.example.wyk.model.request;

import lombok.Data;

@Data
public class SongLyricUpdateRequest {
    private Integer id;
    private String lyric;
}
