package vix.local.api.modules.identity.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserInfo user;
    private List<DepartmentInfo> departments;
    private String route;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentInfo {
        private UUID deptId;
        private String deptName;
        private String deptCode;
        private String schemaTarget;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private UUID id;
        private String email;
        private String fullName;
        private UUID currentDepartmentId;
        private List<String> roles;
        private String phoneNumber;
    }
}
