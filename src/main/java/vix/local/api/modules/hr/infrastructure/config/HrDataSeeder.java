package vix.local.api.modules.hr.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.hr.domain.model.HrDepartment;
import vix.local.api.modules.hr.domain.model.HrUser;
import vix.local.api.modules.hr.domain.repository.HrDepartmentRepository;
import vix.local.api.modules.hr.domain.repository.HrUserRepository;
import vix.local.api.modules.identity.application.port.IdentityPort;
import vix.local.api.modules.identity.domain.model.UserRole;

import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HrDataSeeder implements CommandLineRunner {

    private final HrDepartmentRepository hrDepartmentRepository;
    private final HrUserRepository hrUserRepository;
    private final IdentityPort identityPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (hrDepartmentRepository.findAll().isEmpty() && hrUserRepository.findAll().isEmpty()) {
            log.info("Bắt đầu khởi tạo dữ liệu mặc định (Ban Giám đốc & Giám đốc)...");

            // 1. Tạo phòng ban Ban Giám đốc
            HrDepartment bgd = HrDepartment.builder()
                    .name("Ban Giám đốc")
                    .code("BGD")
                    .description("Phòng ban dành cho Ban Giám đốc")
                    .status("ACTIVE")
                    .build();
            HrDepartment savedBgd = hrDepartmentRepository.save(bgd);

            // 2. Tạo tài khoản Giám đốc
            String encodedPassword = passwordEncoder.encode("123456");
            HrUser director = HrUser.createDirector(
                    "director@vix.local",
                    "Giám đốc",
                    encodedPassword,
                    savedBgd.getId());

            HrUser savedDirector = hrUserRepository.save(director);

            // 2.5 Cấp quyền DEPT_ADMIN cho Giám đốc
            identityPort.upsertUserRole(savedDirector.getId(), savedBgd.getId(), UserRole.DEPT_ADMIN, true);

            log.info("Đã tạo phòng Ban Giám đốc và tài khoản director@vix.local (Mật khẩu: 123456)");
        } else {
            // Check in case director is missing department role
            hrUserRepository.findByEmail("director@vix.local").ifPresent(director -> {
                if (identityPort.getUserRole(director.getId(), director.getDepartmentId()) == null) {
                    hrDepartmentRepository.findByCode("BGD").ifPresent(bgd -> {
                        identityPort.upsertUserRole(director.getId(), bgd.getId(), UserRole.DEPT_ADMIN, true);
                        log.info("Đã tự động cập nhật Role cho director@vix.local (Role: DEPT_ADMIN)");
                    });
                }
            });

            // Repair: tạo Role cho tất cả nhân viên bị thiếu
            repairMissingRoles();
        }
    }

    private void repairMissingRoles() {
        List<HrUser> allUsers = hrUserRepository.findAll();
        int repaired = 0;
        for (HrUser user : allUsers) {
            if (user.getDepartmentId() == null) continue;
            UserRole existingRole = identityPort.getUserRole(user.getId(), user.getDepartmentId());
            if (existingRole == null) {
                UserRole role = "director@vix.local".equals(user.getEmail()) ? UserRole.DEPT_ADMIN : UserRole.MEMBER;
                identityPort.upsertUserRole(user.getId(), user.getDepartmentId(), role, true);
                repaired++;
            }
        }
        if (repaired > 0) {
            log.info("Đã tự động tạo Role cho {} nhân viên bị thiếu", repaired);
        }
    }
}
