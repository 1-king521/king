package com.example.wyk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 从 <a href="https://lrclib.net">LRCLIB</a> 拉取同步歌词（LRC），供本站代理给前端，避免浏览器 CORS。
 * <p>
 * 使用 OkHttp 而非 {@code RestTemplate + HttpURLConnection}：在部分网络/JDK 组合下 TLS 握手行为不同，
 * 可避免「Remote host terminated the handshake」。若仍失败，可配置 HTTP 代理或检查出口能否访问 lrclib.net。
 */
@Slf4j
@Service
public class LrclibLyricsService {

    private final String lrclibBase;
    private final OkHttpClient http;

    public LrclibLyricsService(
            @Value("${yin.lyrics.lrclib-base:https://lrclib.net/api}") String lrclibBase,
            @Value("${yin.lyrics.proxy-host:}") String proxyHost,
            @Value("${yin.lyrics.proxy-port:0}") int proxyPort,
            @Value("${yin.lyrics.connect-timeout-ms:12000}") int connectTimeoutMs,
            @Value("${yin.lyrics.read-timeout-ms:55000}") int readTimeoutMs) {
        String base = lrclibBase == null ? "" : lrclibBase.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (base.isEmpty()) {
            base = "https://lrclib.net/api";
        }
        this.lrclibBase = base;

        OkHttpClient.Builder b =
                new OkHttpClient.Builder()
                        .connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
                        .readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
                        .writeTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
                        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS));

        if (proxyHost != null && !proxyHost.trim().isEmpty() && proxyPort > 0) {
            b.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost.trim(), proxyPort)));
            log.info("LRCLIB client: using HTTP proxy {}:{}", proxyHost.trim(), proxyPort);
        }

        this.http = b.build();
    }

    /**
     * 返回带时间轴的 LRC 文本；找不到则返回 null。
     */
    public String fetchSyncedLyrics(String trackName, String artistName, String albumName, Integer durationSeconds) {
        if (trackName == null || trackName.trim().isEmpty()) {
            return null;
        }
        String artist = artistName == null ? "" : artistName.trim();
        String album = albumName == null ? "" : albumName.trim();

        String fromSearch = trySearch(trackName, artist);
        if (fromSearch != null) {
            return fromSearch;
        }

        /* 歌手名与库内不一致时，仅用歌名再搜一轮 */
        if (!artist.isEmpty()) {
            fromSearch = trySearch(trackName, "");
            if (fromSearch != null) {
                return fromSearch;
            }
        }

        fromSearch = trySearchQueryOnly(trackName);
        if (fromSearch != null) {
            return fromSearch;
        }

        if (durationSeconds != null && durationSeconds > 0 && !album.isEmpty()) {
            return tryGetExact(trackName, artist, album, durationSeconds);
        }
        return null;
    }

    private static String enc(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String httpGet(String url) throws IOException {
        Request request =
                new Request.Builder()
                        .url(url)
                        .header("User-Agent", "Yin-music/1.0 (lrclib.net lyrics proxy)")
                        .build();
        try (Response response = http.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }
            return response.body().string();
        }
    }

    private void logFailure(String phase, Exception e) {
        log.warn("LRCLIB {} failed: {}", phase, e.toString());
        if (log.isDebugEnabled()) {
            log.debug("LRCLIB " + phase, e);
        }
        hintIfSsl(e);
    }

    private static void hintIfSsl(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SSLException) {
                log.warn(
                        "LRCLIB TLS 失败：请确认本机可访问 https://lrclib.net；可尝试 JVM 参数 -Djava.net.preferIPv4Stack=true，"
                                + "或在 application 中配置 yin.lyrics.proxy-host / yin.lyrics.proxy-port（HTTP 代理），"
                                + "或使用可稳定访问外网的网络/VPN。");
                return;
            }
        }
    }

    private String trySearch(String trackName, String artistName) {
        try {
            String url =
                    lrclibBase
                            + "/search?track_name="
                            + enc(trackName.trim())
                            + "&artist_name="
                            + enc(artistName);
            String body = httpGet(url);
            if (body == null) {
                return null;
            }
            String synced = pickFirstSynced(body);
            if (synced != null) {
                return synced;
            }
            String q = trackName.trim() + " " + artistName;
            String url2 = lrclibBase + "/search?q=" + enc(q);
            String body2 = httpGet(url2);
            if (body2 != null) {
                return pickFirstSynced(body2);
            }
        } catch (Exception e) {
            logFailure("search", e);
        }
        return null;
    }

    /** LRCLIB 通用搜索，仅传 q=歌名（适合歌手字段为空或不准时） */
    private String trySearchQueryOnly(String trackName) {
        try {
            String url = lrclibBase + "/search?q=" + enc(trackName.trim());
            String body = httpGet(url);
            if (body == null) {
                return null;
            }
            return pickFirstSynced(body);
        } catch (Exception e) {
            logFailure("search?q=", e);
        }
        return null;
    }

    private String tryGetExact(String trackName, String artistName, String albumName, int durationSeconds) {
        try {
            String url =
                    lrclibBase
                            + "/get?track_name="
                            + enc(trackName.trim())
                            + "&artist_name="
                            + enc(artistName)
                            + "&album_name="
                            + enc(albumName)
                            + "&duration="
                            + durationSeconds;
            String body = httpGet(url);
            if (body == null) {
                return null;
            }
            JSONObject o = JSON.parseObject(body);
            if (o == null) {
                return null;
            }
            String synced = o.getString("syncedLyrics");
            if (synced != null && !synced.trim().isEmpty()) {
                return synced.trim();
            }
            String plain = o.getString("plainLyrics");
            if (plain != null && !plain.trim().isEmpty()) {
                return toPseudoLrc(plain);
            }
        } catch (Exception e) {
            logFailure("get", e);
        }
        return null;
    }

    private static String pickFirstSynced(String jsonArrayBody) {
        JSONArray arr = JSON.parseArray(jsonArrayBody);
        if (arr == null || arr.isEmpty()) {
            return null;
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject o = arr.getJSONObject(i);
            if (o == null) {
                continue;
            }
            String synced = o.getString("syncedLyrics");
            if (synced != null && !synced.trim().isEmpty()) {
                return synced.trim();
            }
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject o = arr.getJSONObject(i);
            if (o == null) {
                continue;
            }
            String plain = o.getString("plainLyrics");
            if (plain != null && !plain.trim().isEmpty()) {
                return toPseudoLrc(plain);
            }
        }
        return null;
    }

    private static String toPseudoLrc(String plain) {
        String[] lines = plain.replace("\r\n", "\n").split("\n");
        StringBuilder sb = new StringBuilder();
        double t = 0;
        for (String line : lines) {
            String s = line.trim();
            if (s.isEmpty()) {
                continue;
            }
            int m = (int) (t / 60);
            double sec = t % 60;
            sb.append(String.format("[%02d:%05.2f]%s%n", m, sec, s));
            t += 4.5;
        }
        return sb.toString().trim();
    }
}
