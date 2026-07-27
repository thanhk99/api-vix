package vix.local.api.modules.permission.infrastructure.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleGroupId implements Serializable {
    private UUID userId;
    private UUID roleGroupId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleGroupId that = (UserRoleGroupId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(roleGroupId, that.roleGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleGroupId);
    }
}
