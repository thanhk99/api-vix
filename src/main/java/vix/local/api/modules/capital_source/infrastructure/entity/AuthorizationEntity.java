package vix.local.api.modules.capital_source.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "authorizations", schema = "capital_source")
@Data
@Builder
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
    private LocalDate ExpiryDate;

    @Column(name = "authed_position")
    private String authedPosition;

    // Phạm vi UQ không có trường DB

    private String phone;
    private String email;
}