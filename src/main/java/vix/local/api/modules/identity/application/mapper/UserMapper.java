package vix.local.api.modules.identity.application.mapper;

import org.springframework.stereotype.Component;
import vix.local.api.modules.identity.api.v1.dto.response.AuthResponse;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.identity.domain.model.UserDepartment;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public AuthResponse.UserInfo toUserInfo(User user, UUID currentDepartmentId, List<UserDepartment> departments) {
        if (user == null) return null;
        
        List<String> roles = departments.stream()
                .map(d -> d.getRole().name())
                .collect(Collectors.toList());

        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .currentDepartmentId(currentDepartmentId)
                .roles(roles)
                .build();
    }
}
