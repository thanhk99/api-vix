package vix.local.api.modules.capital_source.domain.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends RuntimeException {
    private final HttpStatus status;

    public AuthorizationException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public AuthorizationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static AuthorizationException notFound(String message) {
        return new AuthorizationException(HttpStatus.NOT_FOUND, message);
    }

    public static AuthorizationException badRequest(String message) {
        return new AuthorizationException(HttpStatus.BAD_REQUEST, message);
    }
}