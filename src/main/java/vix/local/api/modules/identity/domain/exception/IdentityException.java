package vix.local.api.modules.identity.domain.exception;

import org.springframework.http.HttpStatus;

public class IdentityException extends RuntimeException {
    private final HttpStatus status;

    public IdentityException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static IdentityException notFound(String message) {
        return new IdentityException(HttpStatus.NOT_FOUND, message);
    }

    public static IdentityException unauthorized(String message) {
        return new IdentityException(HttpStatus.UNAUTHORIZED, message);
    }

    public static IdentityException badRequest(String message) {
        return new IdentityException(HttpStatus.BAD_REQUEST, message);
    }
}
