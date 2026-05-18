package com.example.wyk.service;

import com.example.wyk.constant.Constants;
import com.example.wyk.mapper.SingerMapper;
import com.example.wyk.model.domain.Singer;
import com.example.wyk.model.domain.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 为歌曲数据补充 {@link Song#getSingerPic()}，以及歌单曲目 Map 中的 singerPic，便于前端用歌手头像作封面。
 * <p>
 * 本地图片请放在 {@code {user.dir}/img/singerPic/} 下。优先使用 {@code {歌手id}.jpg/.png/...}，
 * 其次使用与库里一致的拼音文件名（如 zhoujielun.jpg）；若歌曲封面指向占位图或磁盘上不存在，则用歌手头像替换 {@link Song#getPic()}。
 */
@Component
@RequiredArgsConstructor
public class SongSingerPicSupport {

    private final SingerMapper singerMapper;

    public void enrichSong(Song song) {
        if (song == null) {
            return;
        }
        if (song.getSingerId() != null) {
            Singer singer = singerMapper.selectById(song.getSingerId());
            if (singer != null) {
                song.setSingerPic(resolvePublicSingerPicUrl(singer));
            }
        }
        normalizeSongCover(song);
    }

    public void enrichSongs(List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        Set<Integer> ids = new HashSet<>();
        for (Song song : songs) {
            if (song != null && song.getSingerId() != null) {
                ids.add(song.getSingerId());
            }
        }
        Map<Integer, String> picBySingerId = ids.isEmpty()
                ? Collections.emptyMap()
                : loadPicBySingerIds(ids);
        for (Song song : songs) {
            if (song != null && song.getSingerId() != null) {
                String pic = picBySingerId.get(song.getSingerId());
                if (pic != null) {
                    song.setSingerPic(pic);
                }
            }
        }
        for (Song song : songs) {
            if (song != null) {
                normalizeSongCover(song);
            }
        }
    }

    /**
     * 歌单曲目接口等返回的 Map 列表（含 singerId），补充 singerPic。
     */
    public void enrichSongMaps(List<Map<String, Object>> songList) {
        if (songList == null || songList.isEmpty()) {
            return;
        }
        Set<Integer> ids = new HashSet<>();
        for (Map<String, Object> m : songList) {
            Integer sid = parseIntId(m.get("singerId"));
            if (sid != null) {
                ids.add(sid);
            }
        }
        Map<Integer, String> picBySingerId = ids.isEmpty()
                ? Collections.emptyMap()
                : loadPicBySingerIds(ids);
        for (Map<String, Object> m : songList) {
            Integer sid = parseIntId(m.get("singerId"));
            if (sid != null) {
                String pic = picBySingerId.get(sid);
                if (pic != null) {
                    m.put("singerPic", pic);
                }
            }
            normalizeSongCoverInMap(m);
        }
    }

    /** 收藏榜等 Map 行：补 singerPic，并把无效歌曲封面换成歌手图 */
    public void enrichRankSongCovers(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        Set<Integer> ids = new HashSet<>();
        for (Map<String, Object> m : rows) {
            Integer sid = parseIntId(m.get("singerId"));
            if (sid != null) {
                ids.add(sid);
            }
        }
        Map<Integer, String> picBySingerId = ids.isEmpty()
                ? Collections.emptyMap()
                : loadPicBySingerIds(ids);
        for (Map<String, Object> m : rows) {
            Integer sid = parseIntId(m.get("singerId"));
            if (sid != null) {
                String sp = picBySingerId.get(sid);
                if (sp != null) {
                    m.put("singerPic", sp);
                }
            }
            normalizeSongCoverInMap(m);
        }
    }

    private void normalizeSongCover(Song song) {
        if (song == null) {
            return;
        }
        if (!needsDiskSongPicFallback(song.getPic())) {
            return;
        }
        String sp = song.getSingerPic();
        if (sp != null && !sp.isBlank()) {
            song.setPic(sp.trim());
        } else {
            song.setPic("/img/avatorImages/user.jpg");
        }
    }

    private void normalizeSongCoverInMap(Map<String, Object> m) {
        if (m == null) {
            return;
        }
        Object pObj = m.get("pic");
        String pic = pObj == null ? "" : pObj.toString();
        if (!needsDiskSongPicFallback(pic)) {
            return;
        }
        Object spObj = m.get("singerPic");
        String singerPic = spObj == null ? "" : spObj.toString().trim();
        if (!singerPic.isEmpty()) {
            m.put("pic", singerPic);
        } else {
            m.put("pic", "/img/avatorImages/user.jpg");
        }
    }

    /**
     * 歌曲 {@code pic} 需替换为歌手头像：空、占位 tubiao、或指向 {@code user.dir/img/songPic} 下不存在的文件。
     */
    private boolean needsDiskSongPicFallback(String pic) {
        if (pic == null || pic.isBlank()) {
            return true;
        }
        String t = pic.trim();
        if ("/img/songPic/tubiao.jpg".equalsIgnoreCase(t)) {
            return true;
        }
        if (!t.startsWith("/img/songPic/")) {
            return false;
        }
        String name = t.substring("/img/songPic/".length());
        if (name.isEmpty() || name.contains("..")) {
            return true;
        }
        Path dir = Paths.get(Constants.PROJECT_PATH, "img", "songPic");
        return !Files.isRegularFile(dir.resolve(name));
    }

    private Map<Integer, String> loadPicBySingerIds(Set<Integer> ids) {
        List<Singer> singers = singerMapper.selectBatchIds(ids);
        Map<Integer, String> map = new HashMap<>();
        if (singers == null) {
            return map;
        }
        for (Singer singer : singers) {
            if (singer != null && singer.getId() != null) {
                String url = resolvePublicSingerPicUrl(singer);
                if (url != null) {
                    map.put(singer.getId(), url);
                }
            }
        }
        return map;
    }

    /**
     * 解析前端可请求的歌手头像 URL：优先磁盘上 {@code img/singerPic/{id}.ext}，否则用库字段（且对 /img/singerPic/ 校验文件存在）。
     * 供歌手列表等接口在返回前就地改写 {@link Singer#setPic(String)}（不写库）。
     */
    public String resolvePublicSingerPicUrl(Singer singer) {
        if (singer == null || singer.getId() == null) {
            return null;
        }
        Path dir = Paths.get(Constants.PROJECT_PATH, "img", "singerPic");
        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) {
            // 目录不可用则仅走库路径
        }
        String idStr = String.valueOf(singer.getId());
        for (String ext : new String[] {".jpg", ".jpeg", ".png", ".webp", ".gif"}) {
            Path file = dir.resolve(idStr + ext);
            if (Files.isRegularFile(file)) {
                return "/img/singerPic/" + idStr + ext;
            }
        }
        String db = singer.getPic();
        if (db == null || db.isBlank()) {
            return "/img/avatorImages/user.jpg";
        }
        String t = db.trim();
        if (t.startsWith("http://") || t.startsWith("https://")) {
            return t;
        }
        if (t.startsWith("/img/singerPic/")) {
            String name = t.substring("/img/singerPic/".length());
            if (!name.isEmpty() && !name.contains("..") && Files.isRegularFile(dir.resolve(name))) {
                return t;
            }
            return "/img/avatorImages/user.jpg";
        }
        return t;
    }

    /** 接口返回前将 {@link Singer#getPic()} 替换为当前环境下可访问地址（不写库）。 */
    public void resolvePicsForResponse(List<Singer> singers) {
        if (singers == null || singers.isEmpty()) {
            return;
        }
        for (Singer s : singers) {
            if (s == null) {
                continue;
            }
            String url = resolvePublicSingerPicUrl(s);
            if (url != null) {
                s.setPic(url);
            }
        }
    }

    private static Integer parseIntId(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number) {
            return ((Number) raw).intValue();
        }
        try {
            return Integer.parseInt(raw.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
