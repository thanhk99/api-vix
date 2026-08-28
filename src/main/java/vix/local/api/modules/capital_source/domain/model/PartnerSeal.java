package vix.local.api.modules.capital_source.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.PartnerSealException;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerSeal {
    private UUID id;
    private UUID partnerId;
    private String sealFileName;
    private String description;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String status;
    private UUID updatedBy;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    public void validateSeal() {
        if (this.effectiveDate == null) {
            throw new PartnerSealException("Ngày hiệu lực không được để trống");
        }
        if (this.expiryDate != null && this.effectiveDate.isAfter(this.expiryDate)) {
            throw new PartnerSealException("Ngày kết thúc phải sau ngày bắt đầu");
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