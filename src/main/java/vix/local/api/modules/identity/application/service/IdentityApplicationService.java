package vix.local.api.modules.identity.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.identity.application.port.IdentityPort;
import vix.local.api.modules.identity.domain.model.User;
import vix.local.api.modules.identity.domain.model.UserRole;
import vix.local.api.modules.identity.domain.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdentityApplicationService implements IdentityPort {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserRole getUserRole(UUID userId, UUID departmentId) {
        return userRepository.findById(userId)
                .filter(u -> departmentId.equals(u.getDepartmentId()))
                .map(User::getDepartmentRole)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, UserRole> getUserRoles(List<UUID> userIds, UUID departmentId) {
        return userIds.stream().collect(Collectors.toMap(
                id -> id,
                id -> {
                    UserRole role = getUserRole(id, departmentId);
                    return role != null ? role : UserRole.MEMBER;
                }
        ));
    }

    @Override
    @Transactional
    public void upsertUserRole(UUID userId, UUID departmentId, UserRole role, boolean isPrimary) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setDepartmentId(departmentId);
            user.setDepartmentRole(role);
            userRepository.save(user);
        });
    }

    @Override
    @Transactional
    public void demoteOldManager(UUID departmentId, UUID newManagerUserId) {
        userRepository.findAll().stream()
                .filter(u -> departmentId.equals(u.getDepartmentId()) && UserRole.DEPT_ADMIN == u.getDepartmentRole())
                .forEach(oldManager -> {
                    if (!oldManager.getId().equals(newManagerUserId)) {
                        oldManager.setDepartmentRole(UserRole.MEMBER);
                        userRepository.save(oldManager);
                    }
                });
    }

    @Override
    @Transactional
    public void deleteUserDepartment(UUID userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setDepartmentId(null);
            user.setDepartmentRole(null);
            user.setRoleGroupId(null);
            userRepository.save(user);
        });
    }
}
