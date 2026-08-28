package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PartnerBankAccount {
    private UUID id;
    private UUID partnerId;
    private String accountNumber;
    private String accountName;
    private String branch;
    private String citadCode;
    private String purpose;
    private String status;
    private String accountType;
    private String openPlace;
    private String depositoryMemberNo;
    private String tradingGateway;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private UUID updatedBy;
    private LocalDateTime updatedAt;

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_DELETED = "DELETED";
    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";

    public void markAsDeleted() {
        this.status = STATUS_DELETED;
    }
}
