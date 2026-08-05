package vix.local.api.modules.permission.application.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.permission.domain.model.ResourceCode;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dịch vụ đọc cấu hình màn hình (screens) của từng phòng ban từ department-templates.yml.
 * Mỗi phòng ban có danh sách màn hình riêng cùng các action được phép trên từng màn hình.
 * HR dùng danh sách này để tạo role_group và gán quyền — không thể gán ngoài phạm vi đã định.
 */
@Slf4j
@Service
public class DepartmentPermissionInitService {

    /**
     * Lấy danh sách màn hình và actions được phép của một phòng ban theo mã phòng ban.
     *
     * @param deptCode mã phòng ban (ví dụ: "NV", "HR", "FINANCE")
     * @return danh sách ScreenPermission, rỗng nếu không tìm thấy cấu hình
     */
    public List<ScreenPermission> getScreensForDept(String deptCode) {
        DeptTemplate template = loadTemplateForDept(deptCode);
        if (template == null) {
            log.warn("Không tìm thấy cấu hình screens cho mã phòng ban: {}", deptCode);
            return Collections.emptyList();
        }

        if (template.getScreens() == null) {
            return Collections.emptyList();
        }

        return template.getScreens().stream()
                .map(s -> {
                    ResourceCode resource = ResourceCode.valueOf(s.getResource());
                    List<ActionCode> actions = s.getActions().stream()
                            .map(ActionCode::valueOf)
                            .collect(Collectors.toList());
                    return new ScreenPermission(resource, actions);
                })
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra xem một action cụ thể có được phép thực hiện trên một resource
     * trong phạm vi phòng ban không.
     *
     * @param deptCode  mã phòng ban
     * @param resource  màn hình cần kiểm tra
     * @param action    action cần kiểm tra
     * @return true nếu được phép
     */
    public boolean isActionAllowed(String deptCode, ResourceCode resource, ActionCode action) {
        return getScreensForDept(deptCode).stream()
                .filter(s -> s.getResource() == resource)
                .anyMatch(s -> s.getAllowedActions().contains(action));
    }

    @SuppressWarnings("unchecked")
    private DeptTemplate loadTemplateForDept(String deptCode) {
        try {
            Yaml yaml = new Yaml();
            ClassPathResource resource = new ClassPathResource("department-templates.yml");
            if (!resource.exists()) {
                log.error("Không tìm thấy file department-templates.yml trong resources.");
                return null;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                Map<String, Object> obj = yaml.load(inputStream);
                List<Map<String, Object>> departments = (List<Map<String, Object>>) obj.get("departments");
                for (Map<String, Object> deptMap : departments) {
                    String code = (String) deptMap.get("code");
                    if (deptCode.equalsIgnoreCase(code)) {
                        DeptTemplate template = new DeptTemplate();
                        template.setCode(code);
                        template.setName((String) deptMap.get("name"));

                        // Parse screens
                        List<Map<String, Object>> screenMaps = (List<Map<String, Object>>) deptMap.get("screens");
                        if (screenMaps != null) {
                            List<ScreenTemplate> screens = new ArrayList<>();
                            for (Map<String, Object> sm : screenMaps) {
                                ScreenTemplate st = new ScreenTemplate();
                                st.setResource((String) sm.get("resource"));
                                st.setActions((List<String>) sm.get("actions"));
                                screens.add(st);
                            }
                            template.setScreens(screens);
                        }
                        return template;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi đọc file cấu hình department-templates.yml", e);
        }
        return null;
    }

    // =========================================================================
    // Internal DTOs (chỉ dùng nội bộ để parse YAML)
    // =========================================================================

    @Data
    private static class DeptTemplate {
        private String code;
        private String name;
        private List<ScreenTemplate> screens;
    }

    @Data
    private static class ScreenTemplate {
        private String resource;
        private List<String> actions;
    }

    // =========================================================================
    // Public DTO (trả về cho service/controller bên ngoài)
    // =========================================================================

    @Data
    public static class ScreenPermission {
        private final ResourceCode resource;
        private final List<ActionCode> allowedActions;
    }
}
