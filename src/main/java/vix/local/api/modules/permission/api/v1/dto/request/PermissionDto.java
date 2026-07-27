package vix.local.api.modules.permission.api.v1.dto.request;

import lombok.Data;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.permission.domain.model.ResourceCode;

import java.util.Set;

@Data
public class PermissionDto {
    private ResourceCode resource;
    private Set<ActionCode> actions;
}
