package vix.local.api.shared.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Xóa CHECK constraint lỗi thời trên cột resource của bảng role_group_permissions.
 *
 * Lý do cần thiết:
 * - Hibernate với ddl-auto=update sinh CHECK constraint từ enum ResourceCode tại
 *   thời điểm tạo bảng.
 * - Mỗi khi enum được bổ sung giá trị mới (VD: CAPITAL_CONFIG), constraint cũ
 *   sẽ từ chối INSERT → ứng dụng lỗi mà không cần restart DB.
 * - Column đã được đổi sang columnDefinition="varchar(255)" để Hibernate không
 *   sinh CHECK mới. Runner này đảm bảo xóa constraint cũ còn tồn tại trong DB.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DropResourceCheckConstraintRunner implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        String sql = "ALTER TABLE shared.role_group_permissions " +
                "DROP CONSTRAINT IF EXISTS role_group_permissions_resource_check";
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
            log.info("Đã xóa CHECK constraint 'role_group_permissions_resource_check' (nếu còn tồn tại).");
        } catch (SQLException e) {
            log.warn("Không thể xóa CHECK constraint: {}", e.getMessage());
        }
    }
}
