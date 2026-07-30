package vix.local.api.modules.capital_source.domain.exception;

public class CreditLimitException extends RuntimeException {
    public CreditLimitException(String message) {
        super(message);
    }

    public CreditLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}