package vix.local.api.modules.permission.domain.model;

import java.util.List;

public enum ResourceCode {
    // Shared
    DASHBOARD(List.of(ActionCode.VIEW)),
    DOCUMENT(List.of(ActionCode.VIEW, ActionCode.CREATE, ActionCode.UPDATE, ActionCode.DELETE)),
    AUDIT_LOG(List.of(ActionCode.VIEW, ActionCode.EXPORT)),

    // HR Module
    HR_USER(List.of(ActionCode.VIEW, ActionCode.CREATE, ActionCode.UPDATE, ActionCode.DELETE)),
    HR_DEPARTMENT(List.of(ActionCode.VIEW, ActionCode.CREATE, ActionCode.UPDATE, ActionCode.DELETE)),

    // Business Modules
    REPORT(List.of(ActionCode.VIEW, ActionCode.EXPORT, ActionCode.APPROVE)),
    PAYROLL(List.of(ActionCode.VIEW, ActionCode.CREATE, ActionCode.UPDATE, ActionCode.APPROVE)),
    MEETING(List.of(ActionCode.VIEW, ActionCode.CREATE, ActionCode.UPDATE, ActionCode.DELETE, ActionCode.APPROVE)),
    
    // Permission Management
    MANAGE_ROLE_GROUP(List.of(ActionCode.VIEW, ActionCode.CREATE, ActionCode.UPDATE, ActionCode.DELETE));

    private final List<ActionCode> allowedActions;

    ResourceCode(List<ActionCode> allowedActions) {
        this.allowedActions = allowedActions;
    }

    public List<ActionCode> getAllowedActions() {
        return allowedActions;
    }
}
