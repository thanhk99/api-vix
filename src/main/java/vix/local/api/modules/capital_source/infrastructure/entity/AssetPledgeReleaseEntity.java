package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_pledge_releases", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetPledgeReleaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pledge_id", nullable = false)
    private Long pledgeId;

    @Column(name = "release_qty", nullable = false)
    private BigDecimal releaseQty;

    @Column(name = "release_value")
    private BigDecimal releaseValue;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "note")
    private String note;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "is_exception_approved")
    private Boolean isExceptionApproved;

    @Column(name = "exception_approver")
    private String exceptionApprover;

    @Column(name = "exception_reason", length = 500)
    private String exceptionReason;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }
}
