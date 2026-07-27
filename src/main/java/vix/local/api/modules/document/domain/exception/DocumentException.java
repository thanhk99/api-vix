package vix.local.api.modules.document.domain.exception;

import org.springframework.http.HttpStatus;

public class DocumentException extends RuntimeException {
    private final HttpStatus status;

    public DocumentException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static DocumentException notFound(String message) {
        return new DocumentException(HttpStatus.NOT_FOUND, message);
    }

    public static DocumentException badRequest(String message) {
        return new DocumentException(HttpStatus.BAD_REQUEST, message);
    }

    public static DocumentException internalError(String message) {
        return new DocumentException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
