package vix.local.api.modules.capital_source.domain.exception;

import org.springframework.http.HttpStatus;

public class KunnException extends RuntimeException {
    private final HttpStatus status;

    public KunnException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public KunnException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public KunnException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static KunnException notFound(String message) {
        return new KunnException(HttpStatus.NOT_FOUND, message);
    }

    public static KunnException badRequest(String message) {
        return new KunnException(HttpStatus.BAD_REQUEST, message);
    }
}
