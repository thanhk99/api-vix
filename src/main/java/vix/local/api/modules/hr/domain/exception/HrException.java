package vix.local.api.modules.hr.domain.exception;

import org.springframework.http.HttpStatus;

public class HrException extends RuntimeException {
    private final HttpStatus status;

    public HrException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static HrException notFound(String message) {
        return new HrException(HttpStatus.NOT_FOUND, message);
    }

    public static HrException badRequest(String message) {
        return new HrException(HttpStatus.BAD_REQUEST, message);
    }
}
