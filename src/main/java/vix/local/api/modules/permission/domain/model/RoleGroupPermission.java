package vix.local.api.modules.permission.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleGroupPermission {
    private UUID id;
    private UUID roleGroupId;
    private ResourceCode resource;
    private Set<ActionCode> actions;
}
