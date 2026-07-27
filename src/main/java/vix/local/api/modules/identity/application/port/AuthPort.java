package vix.local.api.modules.identity.application.port;

public interface AuthPort {
    boolean hasRole(String email, String role);
}
