package vix.local.api.modules.document.application.port;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface StoragePort {
    String store(MultipartFile file, String path);
    InputStream load(String path);
    void delete(String path);
    String getUrl(String path);
    String getPublicUrl(String path);
}
