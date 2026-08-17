package vix.local.api.modules.identity.application.port;

import vix.local.api.modules.identity.domain.model.UserRole;

import java.util.Map;
import java.util.UUID;
import java.util.List;

public interface IdentityPort {
    /**
     * Lấy role của user trong một department
     */
    UserRole getUserRole(UUID userId, UUID departmentId);

    /**
     * Lấy map roles cho danh sách user trong một department (Bulk fetch)
     */
    Map<UUID, UserRole> getUserRoles(List<UUID> userIds, UUID departmentId);

    /**
     * Đổi role hoặc cập nhật phòng ban chính
     */
    void upsertUserRole(UUID userId, UUID departmentId, UserRole role, boolean isPrimary);

    /**
     * Hạ cấp trưởng phòng cũ xuống MEMBER
     */
    void demoteOldManager(UUID departmentId, UUID newManagerUserId);

    /**
     * Xóa user khỏi phòng ban
     */
    void deleteUserDepartment(UUID userId);
}
