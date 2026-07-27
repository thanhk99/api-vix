package vix.local.api.modules.capital_source.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private CategoryGroup group;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;

    /**
     * Factory method — tạo danh mục mới với trạng thái ACTIVE mặc định
     */
    public static Category createNew(String code, String name, String description,
                                     CategoryGroup group, String createdBy) {
        return Category.builder()
                .code(code)
                .name(name)
                .description(description)
                .group(group)
                .status(CategoryStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }

    /**
     * Cập nhật thông tin danh mục
     */
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Ngừng sử dụng danh mục (Soft delete — không xóa cứng)
     */
    public void deactivate() {
        if (this.status == CategoryStatus.INACTIVE) {
            throw new IllegalStateException("Danh mục đã ở trạng thái ngừng sử dụng.");
        }
        this.status = CategoryStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Kích hoạt lại danh mục
     */
    public void activate() {
        if (this.status == CategoryStatus.ACTIVE) {
            throw new IllegalStateException("Danh mục đã ở trạng thái kích hoạt.");
        }
        this.status = CategoryStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
}
