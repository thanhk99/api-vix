package vix.local.api.modules.capital_source.domain.exception;

import org.springframework.http.HttpStatus;

public class CreditLimitException extends RuntimeException {
    private final HttpStatus status;

    public CreditLimitException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public CreditLimitException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public CreditLimitException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static CreditLimitException notFound(String message) {
        return new CreditLimitException(HttpStatus.NOT_FOUND, message);
    }

    public static CreditLimitException badRequest(String message) {
        return new CreditLimitException(HttpStatus.BAD_REQUEST, message);
    }
}