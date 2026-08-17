package vix.local.api.modules.capital_source.domain.exception;

import org.springframework.http.HttpStatus;

public class AssetException extends RuntimeException {
    private final HttpStatus status;

    public AssetException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public AssetException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AssetException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static AssetException notFound(String message) {
        return new AssetException(HttpStatus.NOT_FOUND, message);
    }

    public static AssetException badRequest(String message) {
        return new AssetException(HttpStatus.BAD_REQUEST, message);
    }
}