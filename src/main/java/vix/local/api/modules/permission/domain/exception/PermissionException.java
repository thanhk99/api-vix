package vix.local.api.modules.permission.domain.exception;

import org.springframework.http.HttpStatus;

public class PermissionException extends RuntimeException {
    private final HttpStatus status;

    public PermissionException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static PermissionException roleGroupNotFound(String id) {
        return new PermissionException(HttpStatus.NOT_FOUND, "Nhóm quyền không tồn tại: " + id);
    }

    public static PermissionException roleGroupNameExists(String name) {
        return new PermissionException(HttpStatus.CONFLICT, "Tên nhóm quyền đã tồn tại trong phòng ban: " + name);
    }

    public static PermissionException unauthorizedAction(String message) {
        return new PermissionException(HttpStatus.FORBIDDEN, message);
    }
}
