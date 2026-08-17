package vix.local.api.modules.capital_source.domain.exception;

import org.springframework.http.HttpStatus;

public class PartnerException extends RuntimeException {
    private final HttpStatus status;

    public PartnerException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public PartnerException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public PartnerException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static PartnerException notFound(String message) {
        return new PartnerException(HttpStatus.NOT_FOUND, message);
    }

    public static PartnerException badRequest(String message) {
        return new PartnerException(HttpStatus.BAD_REQUEST, message);
    }
}