package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "credit_limits", schema = "capital_source")
@Data
@Builder
public class CreditLimitEntity {
    @Id
    private UUID id;

    @Column(name = "partner_id")
    private UUID partnerId;

    @Column(name = "limit_id")
    private String limitId;  // Mã hạn mức

    @Column(name = "pool_name")
    private String poolName;  // Tên hạn mức

    @Column(name = "currency")
    private String currency;  // Đơn vị tiền tệ

    @Column(name = "pool_type")
    private String poolType;  // Loại hạn mức

    @Column(name = "total_pool")
    private BigDecimal totalPool;  // Hạn mức tổng

    @Column(name = "used_pool")
    private BigDecimal usedPool;  // Tổng hạn mức đã sử dụng

    @Column(name = "remain_pool")
    private BigDecimal remainPool;  // Tổng hạn mức còn lại

    @Column(name = "start_date")
    private LocalDate startDate;  // Ngày hiệu lực

    @Column(name = "end_date")
    private LocalDate endDate;  // Ngày hết hạn

    private String status;  // Trạng thái
}