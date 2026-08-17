package vix.local.api.modules.identity.application.mapper;

import org.springframework.stereotype.Component;
import vix.local.api.modules.identity.api.v1.dto.response.AuthResponse;
import vix.local.api.modules.identity.domain.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public AuthResponse.UserInfo toUserInfo(User user) {
        if (user == null) return null;
        
        List<String> roles = new ArrayList<>();
        if (user.getDepartmentRole() != null) {
            roles.add(user.getDepartmentRole().name());
        }

        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .currentDepartmentId(user.getDepartmentId())
                .roles(roles)
                .build();
    }
}
