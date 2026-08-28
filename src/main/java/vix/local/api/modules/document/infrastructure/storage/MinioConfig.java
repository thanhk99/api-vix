package vix.local.api.modules.document.infrastructure.storage;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            boolean found = minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(io.minio.MakeBucketArgs.builder().bucket(bucket).build());
                
                String policy = "{\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Action\": [\"s3:GetBucketLocation\", \"s3:ListBucket\"],\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": \"*\",\n" +
                        "      \"Resource\": \"arn:aws:s3:::" + bucket + "\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"Action\": \"s3:GetObject\",\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": \"*\",\n" +
                        "      \"Resource\": \"arn:aws:s3:::" + bucket + "/*\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"Version\": \"2012-10-17\"\n" +
                        "}";
                minioClient.setBucketPolicy(io.minio.SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
            }

            return minioClient;
        } catch (Exception e) {
            throw new RuntimeException("Error initializing Minio client: " + e.getMessage(), e);
        }
    }
}
