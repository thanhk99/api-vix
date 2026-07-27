package vix.local.api.modules.capital_source.domain.exception;

import org.springframework.http.HttpStatus;

public class CategoryException extends RuntimeException {

    private final HttpStatus status;

    public CategoryException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static CategoryException notFound(String id) {
        return new CategoryException(HttpStatus.NOT_FOUND,
                "Không tìm thấy danh mục với id: " + id);
    }

    public static CategoryException duplicateCode(String code, String type) {
        return new CategoryException(HttpStatus.CONFLICT,
                "Mã danh mục '" + code + "' đã tồn tại trong loại " + type);
    }

    public static CategoryException invalidState(String message) {
        return new CategoryException(HttpStatus.CONFLICT, message);
    }
}
