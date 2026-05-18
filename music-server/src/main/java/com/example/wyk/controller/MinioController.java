package com.example.wyk.controller;

import com.example.wyk.constant.Constants;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.catalina.connector.ClientAbortException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Streams objects from MinIO; when MinIO is down or object missing, falls back to on-disk paths
 * used before migration so dev works without a local MinIO.
 * <p>
 * 歌曲 MP3 必须支持 HTTP Range（Accept-Ranges / 206 / Content-Range），否则浏览器无法正确 seek，
 * 表现为一拖进度条就从头播放或无法跳转。
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MinioController {

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    @GetMapping("/{bucket}/singer/img/{fileName:.+}")
    public ResponseEntity<byte[]> getSingerImage(
            @PathVariable String bucket,
            @PathVariable String fileName) {
        if (!bucketName.equals(bucket)) {
            log.debug("singer/img path bucket [{}] != configured [{}], still reading from configured MinIO bucket", bucket, bucketName);
        }
        byte[] bytes = readFromMinio("singer/img/" + fileName);
        if (bytes == null) {
            bytes = readLocalFile(Paths.get(Constants.PROJECT_PATH, "img", "singerPic"), fileName);
        }
        return imageResponse(bytes, fileName);
    }

    @GetMapping("/{bucket}/songlist/{fileName:.+}")
    public ResponseEntity<byte[]> getSongListImage(
            @PathVariable String bucket,
            @PathVariable String fileName) {
        if (!bucketName.equals(bucket)) {
            log.debug("songlist path bucket [{}] != configured [{}], still reading key songlist/{}", bucket, bucketName, fileName);
        }
        byte[] bytes = readFromMinio("songlist/" + fileName);
        if (bytes == null) {
            bytes = readLocalFile(Paths.get(Constants.PROJECT_PATH, "img", "songListPic"), fileName);
        }
        return imageResponse(bytes, fileName);
    }

    @GetMapping("/{bucket}/singer/song/{fileName:.+}")
    public ResponseEntity<byte[]> getSongCoverImage(
            @PathVariable String bucket,
            @PathVariable String fileName) {
        if (!bucketName.equals(bucket)) {
            log.debug("singer/song path bucket [{}] != configured [{}]", bucket, bucketName);
        }
        byte[] bytes = readFromMinio("singer/song/" + fileName);
        if (bytes == null) {
            bytes = readLocalFile(Paths.get(Constants.PROJECT_PATH, "img", "songPic"), fileName);
        }
        return imageResponse(bytes, fileName);
    }

    /**
     * 音频：流式输出 + Range，避免整文件读入内存；满足浏览器 / &lt;audio&gt; 拖动进度。
     */
    @GetMapping("/{bucket}/{fileName:.+}")
    @ResponseBody
    public void getMusic(HttpServletRequest request,
                         HttpServletResponse response,
                         @PathVariable String bucket,
                         @PathVariable String fileName) {
        if (!bucketName.equals(bucket)) {
            log.debug("audio path bucket [{}] != configured [{}]", bucket, bucketName);
        }
        if (fileName == null || fileName.isEmpty() || fileName.contains("..")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Long minioSize = statMinioObjectSize(fileName);
        Path localPath = null;
        final long totalSize;
        final boolean fromMinio;

        if (minioSize != null) {
            totalSize = minioSize;
            fromMinio = true;
        } else {
            localPath = resolveLocalSongFile(fileName);
            if (localPath == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try {
                totalSize = Files.size(localPath);
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            fromMinio = false;
        }

        if (totalSize <= 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ByteRange br = ByteRange.fromHeader(request.getHeader(HttpHeaders.RANGE), totalSize);
        if (br.unsatisfiable) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + totalSize);
            return;
        }

        final long start = br.start;
        final long end = br.end;
        final long contentLength = end - start + 1;

        response.setContentType("audio/mpeg");
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.inline().filename(fileName, StandardCharsets.UTF_8).build().toString());

        try {
            if (br.isFullFile) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentLengthLong(totalSize);
                writeAudioRange(response.getOutputStream(), fileName, fromMinio, localPath, start, totalSize, totalSize);
            } else {
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setContentLengthLong(contentLength);
                response.setHeader(HttpHeaders.CONTENT_RANGE,
                        String.format("bytes %d-%d/%d", start, end, totalSize));
                writeAudioRange(response.getOutputStream(), fileName, fromMinio, localPath, start, contentLength, totalSize);
            }
        } catch (IOException e) {
            if (isClientDisconnected(e)) {
                log.debug("Client aborted audio stream: {}", fileName);
            } else {
                log.warn("stream music failed for {}: {}", fileName, e.getMessage());
            }
        }
    }

    @GetMapping("/img/avatorImages/{fileName:.+}")
    public ResponseEntity<byte[]> getAvatarImage(@PathVariable String fileName) {
        byte[] bytes = readFromMinio("img/avatorImages/" + fileName);
        if (bytes == null) {
            bytes = readLocalFile(Paths.get(Constants.PROJECT_PATH, "img", "avatorImages"), fileName);
        }
        if (bytes == null) {
            bytes = readClasspathStatic("static/img/avatorImages/" + fileName);
        }
        return imageResponse(bytes, fileName);
    }

    /**
     * 浏览器 / &lt;audio&gt; 在 seek、切歌、刷新页面时会主动断开，Tomcat 抛出 {@link ClientAbortException}。
     * 这不表示 MinIO 或服务端故障，不应打 ERROR。
     */
    private static boolean isClientDisconnected(Throwable t) {
        for (Throwable c = t; c != null; c = c.getCause()) {
            if (c instanceof ClientAbortException) {
                return true;
            }
            String m = c.getMessage();
            if (m != null) {
                if (m.contains("Broken pipe")
                        || m.contains("Connection reset")
                        || m.contains("中止了一个已建立的连接")
                        || m.contains("Connection aborted")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void writeAudioRange(OutputStream out, String fileName, boolean fromMinio, Path localPath,
                                 long start, long length, long totalSize) throws IOException {
        if (fromMinio) {
            GetObjectArgs.Builder b = GetObjectArgs.builder().bucket(bucketName).object(fileName);
            if (start == 0 && length == totalSize) {
                try (InputStream in = minioClient.getObject(b.build())) {
                    IOUtils.copy(in, out);
                } catch (Exception e) {
                    if (isClientDisconnected(e)) {
                        log.debug("Client aborted while streaming from MinIO (full): {}", fileName);
                        return;
                    }
                    log.error("Failed to get object from MinIO: {}", fileName, e);
                    throw new IOException("MinIO getObject failed", e);
                }
            } else {
                try (InputStream in = minioClient.getObject(b.offset(start).length(length).build())) {
                    IOUtils.copy(in, out);
                } catch (Exception e) {
                    if (isClientDisconnected(e)) {
                        log.debug("Client aborted while streaming from MinIO (range): {} [{}-{}]",
                                fileName, start, length);
                        return;
                    }
                    log.error("Failed to get object range from MinIO: {} [offset={}, length={}]",
                            fileName, start, length, e);
                    throw new IOException("MinIO getObject with range failed", e);
                }
            }
            return;
        }
        try (InputStream in = Files.newInputStream(localPath)) {
            IOUtils.skipFully(in, start);
            IOUtils.copyLarge(in, out, 0, length);
        } catch (IOException e) {
            if (isClientDisconnected(e)) {
                log.debug("Client aborted while streaming local file: {}", fileName);
                return;
            }
            throw e;
        }
    }

    private Long statMinioObjectSize(String objectKey) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());
            return stat.size();
        } catch (Exception e) {
            log.debug("MinIO statObject skipped for {}: {}", objectKey, e.getMessage());
            return null;
        }
    }

    private static Path resolveLocalSongFile(String fileName) {
        if (fileName == null || fileName.contains("..")) {
            return null;
        }
        Path base = Paths.get(Constants.PROJECT_PATH, "song").toAbsolutePath().normalize();
        Path target = base.resolve(fileName).normalize();
        if (!target.startsWith(base) || !Files.isRegularFile(target)) {
            return null;
        }
        return target;
    }

    private static final class ByteRange {
        final long start;
        final long end;
        final boolean isFullFile;
        final boolean unsatisfiable;

        ByteRange(long start, long end, boolean isFullFile, boolean unsatisfiable) {
            this.start = start;
            this.end = end;
            this.isFullFile = isFullFile;
            this.unsatisfiable = unsatisfiable;
        }

        static ByteRange fromHeader(String rangeHeader, long size) {
            if (size <= 0) {
                return new ByteRange(0, -1, true, true);
            }
            if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
                return new ByteRange(0, size - 1, true, false);
            }
            String spec = rangeHeader.substring(6).trim();
            int dash = spec.indexOf('-');
            if (dash < 0) {
                return new ByteRange(0, size - 1, true, false);
            }
            String startPart = spec.substring(0, dash);
            String endPart = spec.substring(dash + 1);

            long start;
            long end;

            try {
                if (startPart.isEmpty()) {
                    if (endPart.isEmpty()) {
                        return new ByteRange(0, size - 1, true, false);
                    }
                    long suffix = Long.parseLong(endPart);
                    if (suffix <= 0) {
                        return new ByteRange(0, size - 1, true, false);
                    }
                    start = Math.max(0, size - suffix);
                    end = size - 1;
                } else {
                    start = Long.parseLong(startPart);
                    if (endPart.isEmpty()) {
                        end = size - 1;
                    } else {
                        end = Long.parseLong(endPart);
                    }
                }
            } catch (NumberFormatException e) {
                return new ByteRange(0, size - 1, true, false);
            }

            if (start >= size) {
                return new ByteRange(0, 0, false, true);
            }
            end = Math.min(end, size - 1);
            if (start > end) {
                return new ByteRange(0, 0, false, true);
            }
            boolean full = start == 0 && end == size - 1;
            return new ByteRange(start, end, full, false);
        }
    }

    private ResponseEntity<byte[]> imageResponse(byte[] bytes, String fileName) {
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(guessImageMediaType(fileName));
        /* 避免换图后浏览器长期用旧缓存 */
        headers.add(HttpHeaders.CACHE_CONTROL, "public, max-age=0, must-revalidate");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private static MediaType guessImageMediaType(String fileName) {
        if (fileName == null) {
            return MediaType.IMAGE_JPEG;
        }
        String n = fileName.toLowerCase(Locale.ROOT);
        if (n.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (n.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        if (n.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        if (n.endsWith(".svg")) {
            return MediaType.parseMediaType("image/svg+xml");
        }
        return MediaType.IMAGE_JPEG;
    }

    private byte[] readFromMinio(String objectKey) {
        try (InputStream in = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(objectKey).build())) {
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            log.debug("MinIO getObject skipped for {}: {}", objectKey, e.getMessage());
            return null;
        }
    }

    private static byte[] readLocalFile(Path directory, String fileName) {
        if (fileName == null || fileName.isEmpty() || fileName.contains("..")) {
            return null;
        }
        try {
            Path base = directory.toAbsolutePath().normalize();
            Path target = base.resolve(fileName).normalize();
            if (!target.startsWith(base) || !Files.isRegularFile(target)) {
                return null;
            }
            return Files.readAllBytes(target);
        } catch (IOException e) {
            return null;
        }
    }

    /** Default avatars bundled under {@code src/main/resources/static/...} (not MinIO / not cwd img/). */
    private static byte[] readClasspathStatic(String classpathLocation) {
        if (classpathLocation == null || classpathLocation.contains("..")) {
            return null;
        }
        ClassPathResource res = new ClassPathResource(classpathLocation);
        if (!res.exists()) {
            return null;
        }
        try (InputStream in = res.getInputStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            return null;
        }
    }
}
