package vix.local.api.modules.audit.domain.exception;

import org.springframework.http.HttpStatus;

public class AuditException extends RuntimeException {
    private final HttpStatus status;

    public AuditException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static AuditException badRequest(String message) {
        return new AuditException(HttpStatus.BAD_REQUEST, message);
    }
}
