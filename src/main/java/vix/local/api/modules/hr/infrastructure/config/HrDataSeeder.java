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
import vix.local.api.modules.identity.domain.model.UserDepartment;
import vix.local.api.modules.identity.domain.model.UserRole;
import vix.local.api.modules.identity.domain.repository.UserDepartmentRepository;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HrDataSeeder implements CommandLineRunner {

    private final HrDepartmentRepository hrDepartmentRepository;
    private final HrUserRepository hrUserRepository;
    private final UserDepartmentRepository userDepartmentRepository;
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

            // 2.5 Cấp quyền SUPER_ADMIN cho Giám đốc
            UserDepartment ud = UserDepartment.builder()
                    .userId(savedDirector.getId())
                    .departmentId(savedBgd.getId())
                    .role(UserRole.SUPER_ADMIN)
                    .isPrimary(true)
                    .build();
            userDepartmentRepository.save(ud);

            // 3. Cập nhật trưởng phòng BGD
            savedBgd.setManagerId(savedDirector.getId());
            hrDepartmentRepository.save(savedBgd);

            log.info("Đã tạo phòng Ban Giám đốc và tài khoản director@vix.local (Mật khẩu: 123456)");
        } else {
            // Check in case director is missing UserDepartment
            hrUserRepository.findByEmail("director@vix.local").ifPresent(director -> {
                if (userDepartmentRepository.findByUserId(director.getId()).isEmpty()) {
                    hrDepartmentRepository.findByCode("BGD").ifPresent(bgd -> {
                        userDepartmentRepository.upsert(director.getId(), bgd.getId(), UserRole.SUPER_ADMIN, true);
                        log.info("Đã tự động cập nhật UserDepartment cho director@vix.local (Role: SUPER_ADMIN)");
                    });
                }
            });

            // Repair: tạo UserDepartment cho tất cả nhân viên bị thiếu
            repairMissingUserDepartments();
        }
    }

    private void repairMissingUserDepartments() {
        List<HrUser> allUsers = hrUserRepository.findAll();
        int repaired = 0;
        for (HrUser user : allUsers) {
            if (user.getDepartmentId() == null) continue;
            List<vix.local.api.modules.identity.domain.model.UserDepartment> existing =
                    userDepartmentRepository.findByUserId(user.getId());
            if (existing.isEmpty()) {
                UserRole role = "director@vix.local".equals(user.getEmail()) ? UserRole.SUPER_ADMIN : UserRole.MEMBER;
                userDepartmentRepository.upsert(user.getId(), user.getDepartmentId(), role, true);
                repaired++;
            }
        }
        if (repaired > 0) {
            log.info("Đã tự động tạo UserDepartment cho {} nhân viên bị thiếu", repaired);
        }
    }
}
