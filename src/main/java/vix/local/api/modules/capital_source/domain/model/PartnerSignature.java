package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.PartnerSignatureException;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PartnerSignature {
    private UUID id;
    private UUID partnerId;
    private UUID documentId;
    private String signFileName;
    private String signType;
    private String description;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String status;
    private UUID updatedBy;
    private String updatedByName;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    public void validateSignature() {
        if (this.effectiveDate == null) {
            throw new PartnerSignatureException("Ngày hiệu lực không được để trống");
        }
        if (this.expiryDate != null && !this.expiryDate.isAfter(this.effectiveDate)) {
            throw new PartnerSignatureException("Ngày hết hạn phải lớn hơn ngày hiệu lực");
        }
    }

    public void updateStatus() {
        if ("DELETED".equals(this.status)) return;
        if (this.expiryDate != null && this.expiryDate.isBefore(LocalDate.now())) {
            this.status = "DUEDATE";
        } else {
            this.status = "APPROVED";
        }
    }
    
    public void markAsDeleted() {
        this.status = "DELETED";
    }
}
