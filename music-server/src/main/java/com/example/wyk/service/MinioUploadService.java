package com.example.wyk.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Uploads objects to MinIO (migrated from legacy MinioUploadController static helpers).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioUploadService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void ensureBucket() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MinIO: created bucket [{}]", bucketName);
            }
        } catch (Exception e) {
            log.error("MinIO: cannot ensure bucket [{}] — uploads will fail until MinIO is reachable. {}",
                    bucketName, e.getMessage());
            log.debug("MinIO ensureBucket detail", e);
        }
    }

    /**
     * @return object key stored in bucket, or {@code null} if upload failed
     */
    public String uploadSongFile(MultipartFile file) {
        String objectName = buildUniqueSongObjectKey(file.getOriginalFilename());
        return putObject(objectName, file) ? objectName : null;
    }

    private static String buildUniqueSongObjectKey(String original) {
        String suffix = (original == null || original.isBlank()) ? "audio.bin" : original.trim().replaceAll("[/\\\\]", "_");
        return System.currentTimeMillis() + "_" + suffix;
    }

    public boolean uploadSingerImg(MultipartFile file, String objectFileName) {
        return putObject("singer/img/" + objectFileName, file);
    }

    public boolean uploadSongListImg(MultipartFile file, String objectFileName) {
        return putObject("songlist/" + objectFileName, file);
    }

    public boolean uploadSongCoverImg(MultipartFile file, String objectFileName) {
        return putObject("singer/song/" + objectFileName, file);
    }

    public boolean uploadAvatarImg(MultipartFile file, String objectFileName) {
        return putObject("img/avatorImages/" + objectFileName, file);
    }

    private boolean putObject(String objectName, MultipartFile file) {
        if (objectName == null || objectName.isBlank()) {
            log.warn("MinIO putObject skipped: empty object name");
            return false;
        }
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }
        try (InputStream inputStream = file.getInputStream()) {
            long size = file.getSize();
            if (size < 0) {
                size = inputStream.available();
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            return true;
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("MinIO putObject failed bucket={} object={} : {}", bucketName, objectName, e.getMessage());
            log.debug("MinIO putObject stack trace", e);
            return false;
        } catch (Exception e) {
            log.error("MinIO putObject unexpected error bucket={} object={}", bucketName, objectName, e);
            return false;
        }
    }
}
