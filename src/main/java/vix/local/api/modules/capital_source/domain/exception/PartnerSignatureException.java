package vix.local.api.modules.capital_source.domain.exception;

import org.springframework.http.HttpStatus;

public class PartnerSignatureException extends RuntimeException {
    private final HttpStatus status;

    public PartnerSignatureException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public PartnerSignatureException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static PartnerSignatureException notFound(String message) {
        return new PartnerSignatureException(HttpStatus.NOT_FOUND, message);
    }

    public static PartnerSignatureException badRequest(String message) {
        return new PartnerSignatureException(HttpStatus.BAD_REQUEST, message);
    }
}
