package vix.local.api.modules.permission.domain.model;

import java.util.List;

public enum ModulePermission {
    // 1. Quản lý đối tác và Hạn mức
    PARTNER_AND_CREDIT_LIMIT(List.of("C", "R", "U", "D", "A")),

    // 2. Quản lý hợp đồng vay và KUNN
    LOAN_CONTRACT_AND_KUNN(List.of("C", "R", "U", "D", "A")),

    // 3. Quản lý dư nợ và thanh toán
    DEBT_MANAGEMENT_AND_PAYMENT(List.of("C", "R", "U", "D", "A")),

    // 4. Quản lý tài sản
    ASSET_MANAGEMENT(List.of("C", "R", "U", "D", "A")),

    // 5. Batch cuối ngày
    DAILY_BATCH_MONITORING(List.of("R", "R", "R", "R", "R")),

    // 6. Báo cáo và tra cứu
    REPORT_AND_INQUIRY(List.of("R", "R", "R", "R", "R")),

    // 7. Phân quyền và kiểm soát
    PERMISSION_AND_CONTROLS(List.of("C", "R", "U", "D", "A"));

    private final List<String> permissions;

    ModulePermission(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    // Check if a specific permission is allowed for this module
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}