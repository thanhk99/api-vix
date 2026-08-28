package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "partners", schema = "capital_source")
@Data
public class PartnerEntity {
    @Id
    private UUID id;
    
    @Column(name = "cus_id")
    private String cusId;
    
    @Column(name = "branch_cus_id")
    private String branchCusId;
    
    @Column(name = "cus_name")
    private String cusName;
    
    @Column(name = "short_name")
    private String shortName;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "id_code")
    private String idCode;
    
    @Column(name = "fist_issue_date")
    private LocalDate fistIssueDate;
    
    @Column(name = "last_issue_date")
    private LocalDate lastIssueDate;
    
    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;
    
    @Column(name = "issue_by")
    private String issueBy;
    
    @Column(name = "change_count")
    private Integer changeCount;
    
    
    @Column(name = "op_liscense_no")
    private String opLiscenseNo;
    
    @Column(name = "op_issue_date")
    private LocalDate opIssueDate;
    
    private String mobile;
    
    private String email;
    
    private String website;
    
    private String fax;
    
    @Column(name = "op_issue_by")
    private String opIssueBy;
    
    @Column(name = "general_note", columnDefinition = "TEXT")
    private String generalNote;
    
    @Column(name = "cus_type")
    private String cusType;
    
    @Column(name = "business_type")
    private String businessType;
    
    @Column(name = "professional_investor")
    private Boolean professionalInvestor;
    
    @Column(name = "professional_start_date")
    private LocalDate professionalStartDate;
    
    @Column(name = "professional_end_date")
    private LocalDate professionalEndDate;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    @Column(name = "depository_member_code")
    private String depositoryMemberCode;
    
    @Column(name = "trading_gateway")
    private String tradingGateway;
    
    @Column(name = "status")
    private String status;

    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_by")
    private UUID createdBy;
    
    @Column(name = "updated_by")
    private UUID updatedBy;
    
    @Column(name = "last_updated")
    private LocalDate lastUpdated;
    
    @Column(name = "approved_by")
    private UUID approvedBy;
    
    @Column(name = "approved_at")
    private java.time.LocalDateTime approvedAt;

    @Column(name = "total_pool")
    private java.math.BigDecimal totalPool;

    @Column(name = "used_pool")
    private java.math.BigDecimal usedPool;

    @Column(name = "remain_pool")
    private java.math.BigDecimal remainPool;
    
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
        if (this.lastUpdated == null) {
            this.lastUpdated = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDate.now();
    }
}
