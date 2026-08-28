package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PartnerResponseDto {
    private UUID id;
    private String cusId;
    private String branchCusId;
    private String cusName;
    private String shortName;
    private String address;
    private String idCode;
    private LocalDate fistIssueDate;
    private LocalDate lastIssueDate;
    private String changeReason;
    private String issueBy;
    private Integer changeCount;
    private String opLiscenseNo;
    private LocalDate opIssueDate;
    private String mobile;
    private String email;
    private String website;

    private String cusType;
    private String businessType;
    private Boolean professionalInvestor;
    private LocalDate professionalStartDate;
    private LocalDate professionalEndDate;
    private String note;
    private String depositoryMemberCode;
    private String tradingGateway;

    private String status;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDate lastUpdated;
    private String approvedBy;
    private java.time.LocalDateTime approvedAt;

    private java.math.BigDecimal totalPool;
    private java.math.BigDecimal usedPool;
    private java.math.BigDecimal remainPool;
}
