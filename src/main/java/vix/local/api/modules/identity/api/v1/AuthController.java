package vix.local.api.modules.identity.api.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vix.local.api.modules.identity.api.v1.dto.request.LoginRequest;
import vix.local.api.modules.identity.api.v1.dto.request.SelectDepartmentRequest;
import vix.local.api.modules.identity.api.v1.dto.response.AuthResponse;
import vix.local.api.modules.identity.application.service.AuthApplicationService;
import vix.local.api.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/identity/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API cho đăng nhập và xác thực User")
public class AuthController {

    private final AuthApplicationService authService;

    @Operation(summary = "Đăng nhập hệ thống", description = "Đăng nhập và nhận Token. Nếu thuộc nhiều phòng ban, sẽ trả về danh sách phòng ban.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }

    @Operation(summary = "Chọn phòng ban", description = "Được gọi khi user có nhiều phòng ban và chọn 1 phòng ban. Yêu cầu gửi kèm AccessToken ban đầu (hoặc truyền email từ FE, tạm thời dùng JWT cũ hoặc gửi email).")
    @PostMapping("/select-department")
    public ResponseEntity<ApiResponse<AuthResponse>> selectDepartment(
            @Valid @RequestBody SelectDepartmentRequest request) {
        // We will get email from SecurityContext or Token
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
                .getName();
        return ResponseEntity.ok(ApiResponse.success(authService.selectDepartment(email, request.getDeptId())));
    }
}
