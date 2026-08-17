package vix.local.api.modules.identity.domain.model;

public enum UserRole {
    DIRECTOR,    // Giám đốc: full access toàn bộ hệ thống, không giới hạn phòng ban
    DEPT_ADMIN,  // Trưởng phòng: full access trong phòng ban của mình
    MEMBER       // Nhân viên: theo permission được cấp
}
