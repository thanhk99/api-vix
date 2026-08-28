package vix.local.api.modules.capital_source.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.AuthorizationException;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authorization {
    private UUID id;
    private UUID partnerId; // ID cua doi tac lien quan
    private Integer seqId;
    private String authType;
    private UUID parentAuthId; // Uy quyen cap tren
    private String authName; // Ten nguoi UQ
    private String authPosition; // Chuc vu nguoi uy quyen
    private String authidNo; // CC/CCCD nguoi uy quyen
    private LocalDate authissueDate; // Ngay cap nguoi uy quyen
    private String authedName; // Ten nguoi duoc uy quyen
    private String authedIdNo; // CC/CCCD nguoi duoc uy quyen
    private LocalDate authedIssueDate; // Ngay cap nguoi duoc uy quyen
    private String authedIssuePlace; // Noi cap CCCD nguoi duoc uy quyen
    private String issuePlace; // Noi cap
    private String authNo; // So giay to
    private LocalDate effDate; // Ngay hieu luc
    private LocalDate expiryDate; // Ngay het han
    private String authedPosition; // Chuc vu nguoi duoc uy quyen
    private String scope; // Pham vi UQ
    private String note; // Ghi chu
    private String phone; // SĐT
    private String email; // Email
    private String status; // Trang thai

    public void validateAuthorization() {
        if (this.authidNo == null || this.authidNo.trim().isEmpty()) {
            throw new AuthorizationException("Số CCCD người đại diện không được để trống");
        }
        
        if ("AUTHORIZATION".equals(this.authType)) {
            if (this.authedIdNo == null || this.authedIdNo.trim().isEmpty()) {
                throw new AuthorizationException("Số CCCD người nhận ủy quyền không được để trống");
            }
            if (this.effDate == null) {
                throw new AuthorizationException("Ngày hiệu lực không được để trống");
            }
        }
    }
    
    public void updateStatus() {
        if ("DELETED".equals(this.status)) return;
        
        if ("LEGAL_REP".equals(this.authType)) {
            // Legal Representative status is manually set (e.g. ACTIVE / INACTIVE)
            return;
        }
        
        // Auto-calculate for AUTHORIZATION
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
