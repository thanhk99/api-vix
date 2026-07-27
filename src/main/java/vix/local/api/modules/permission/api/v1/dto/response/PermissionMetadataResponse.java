package vix.local.api.modules.permission.api.v1.dto.response;

import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionMetadataResponse {
    private ResourceCode resource;
    private List<ActionCode> allowedActions;
}
