package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.PartnerSecuritiesAccountException;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PartnerSecuritiesAccount {
    private UUID id;
    private UUID partnerId;
    private String accountNumber;
    private String accountName;
    private String tradingGateways;
    private String status;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    public void validate() {
        if (this.accountNumber == null || this.accountNumber.trim().isEmpty()) {
            throw new PartnerSecuritiesAccountException("Số tài khoản không được để trống");
        }
    }

    public void markAsDeleted() {
        this.status = "DELETED";
    }
}