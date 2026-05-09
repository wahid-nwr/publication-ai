package io.wahid.knowledge.infrastructure.storage;

import io.wahid.knowledge.infrastructure.config.AppConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

public class R2Client {

    private final S3Client s3;

    public R2Client() {
        this.s3 = S3Client.builder()
                .endpointOverride(
                        URI.create(AppConfig.r2AccessUrl())
                )
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        AppConfig.r2AccessKey(),
                                        AppConfig.r2SecretKey()
                                )
                        )
                )
                .region(Region.of("auto"))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .chunkedEncodingEnabled(false) // VERY IMPORTANT
                                .build()
                )
                .build();
    }

    public void upload(String bucket, String key, String contentType, Path path) {
        File file = path.toFile();
        long size = file.length();
        if (size <= 0) {
            throw new IllegalArgumentException("Content length must be known for R2 uploads");
        }
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .contentLength(size)
                        .build(),
                RequestBody.fromFile(file)
        );
    }

    public void upload(String bucket, String key, InputStream data, long size, String contentType) {
        if (size <= 0) {
            throw new IllegalArgumentException("Content length must be known for R2 uploads");
        }

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength(size)
                .contentType(contentType)
                .build();

        s3.putObject(
                request,
                RequestBody.fromInputStream(data, size)
        );
    }

    public InputStream download(String bucket, String key) {
        return s3.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        );
    }
}
