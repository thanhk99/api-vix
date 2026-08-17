package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "authorizations", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationEntity {
    @Id
    private UUID id;

    @Column(name = "partner_id")
    private UUID partnerId;

    private Integer seqId;

    @Column(name = "auth_name")
    private String authName;

    @Column(name = "auth_position")
    private String authPosition;

    @Column(name = "authid_no")
    private String authidNo;

    @Column(name = "authissue_date")
    private LocalDate authissueDate;

    @Column(name = "authed_name")
    private String authedName;

    @Column(name = "authed_idno")
    private String authedIdNo;

    @Column(name = "authed_issue_date")
    private LocalDate authedIssueDate;

    @Column(name = "issue_place")
    private String issuePlace;

    @Column(name = "auth_no")
    private String authNo;

    @Column(name = "eff_date")
    private LocalDate effDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "authed_position")
    private String authedPosition;

    @Column(name = "scope")
    private String scope;

    @Column(name = "status")
    private String status;

    private String phone;
    private String email;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch();
        }
        if (this.createdAt == null) {
            this.createdAt = java.time.LocalDateTime.now();
        }
        this.updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
}