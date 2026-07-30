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
    
    private String address;
    
    @Column(name = "id_code")
    private String idCode;
    
    @Column(name = "fist_issue_date")
    private LocalDate fistIssueDate;
    
    @Column(name = "last_issue_date")
    private LocalDate lastIssueDate;
    
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
    
    private String status;
    
    @Column(name = "created_by")
    private UUID createdBy;
    
    @Column(name = "updated_by")
    private UUID updatedBy;
    
    @Column(name = "last_updated")
    private LocalDate lastUpdated;
}