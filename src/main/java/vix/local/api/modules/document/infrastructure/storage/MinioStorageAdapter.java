package vix.local.api.modules.document.infrastructure.storage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vix.local.api.modules.document.domain.exception.DocumentException;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioStorageAdapter implements StoragePort {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String store(MultipartFile file, String path) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return path;
        } catch (Exception e) {
            throw DocumentException.internalError("Lỗi lưu trữ tài liệu: " + e.getMessage());
        }
    }

    @Override
    public InputStream load(String path) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            throw DocumentException.internalError("Lỗi đọc tài liệu: " + e.getMessage());
        }
    }

    @Override
    public void delete(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            throw DocumentException.internalError("Lỗi xóa tài liệu: " + e.getMessage());
        }
    }

    @Override
    public String getUrl(String path) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(path)
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            throw DocumentException.internalError("Lỗi tạo URL tải xuống: " + e.getMessage());
        }
    }
}
