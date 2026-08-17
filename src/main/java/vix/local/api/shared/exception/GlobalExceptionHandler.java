package vix.local.api.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vix.local.api.modules.audit.domain.exception.AuditException;
import vix.local.api.modules.capital_source.domain.exception.AssetException;
import vix.local.api.modules.capital_source.domain.exception.AuthorizationException;
import vix.local.api.modules.capital_source.domain.exception.CategoryException;
import vix.local.api.modules.capital_source.domain.exception.CreditLimitException;
import vix.local.api.modules.capital_source.domain.exception.PartnerException;
import vix.local.api.modules.capital_source.domain.exception.PartnerSignatureException;
import vix.local.api.modules.document.domain.exception.DocumentException;
import vix.local.api.modules.hr.domain.exception.HrException;
import vix.local.api.modules.identity.domain.exception.IdentityException;
import vix.local.api.modules.permission.domain.exception.PermissionException;
import vix.local.api.shared.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Dữ liệu không hợp lệ");
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    @ExceptionHandler(HrException.class)
    public ResponseEntity<ApiResponse<Void>> handleHrException(HrException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IdentityException.class)
    public ResponseEntity<ApiResponse<Void>> handleIdentityException(IdentityException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(PartnerException.class)
    public ResponseEntity<ApiResponse<Void>> handlePartnerException(PartnerException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CreditLimitException.class)
    public ResponseEntity<ApiResponse<Void>> handleCreditLimitException(CreditLimitException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AssetException.class)
    public ResponseEntity<ApiResponse<Void>> handleAssetException(AssetException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationException(AuthorizationException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(PartnerSignatureException.class)
    public ResponseEntity<ApiResponse<Void>> handlePartnerSignatureException(PartnerSignatureException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<ApiResponse<Void>> handleCategoryException(CategoryException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ApiResponse<Void>> handlePermissionException(PermissionException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DocumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleDocumentException(DocumentException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AuditException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuditException(AuditException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Không có quyền truy cập"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity.internalServerError().body(ApiResponse.error("Lỗi hệ thống, vui lòng thử lại sau. Chi tiết: " + ex.getMessage()));
    }
}
