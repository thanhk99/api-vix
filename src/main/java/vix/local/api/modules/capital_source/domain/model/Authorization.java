package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.AuthorizationException;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Authorization {
    private UUID id;
    private UUID partnerId; // ID của đối tác liên quan
    private Integer seqId; // Số thứ tự
    private String authName; // Tên người UQ
    private String authPosition; // Chức vụ người uỷ quyền
    private String authidNo; // CC/CCCD người uỷ quyền
    private LocalDate authissueDate; // Ngày cấp người uỷ quyền
    private String authedName; // Tên người được uỷ quyền
    private String authedIdNo; // CC/CCCD người được uỷ quyền
    private LocalDate authedIssueDate; // Ngày cấp người được uỷ quyền
    private String issuePlace; // Nơi cấp
    private String authNo; // Số giấy tờ
    private LocalDate effDate; // Ngày hiệu lực
    private LocalDate expiryDate; // Ngày hết hạn
    private String authedPosition; // Chức vụ người được uỷ quyền
    private String scope; // Phạm vi UQ
    private String phone; // SĐT
    private String email; // Email
    private String status; // Trạng thái

    public void validateAuthorization() {
        if (this.authidNo == null || this.authidNo.isEmpty()) {
            throw new AuthorizationException("CC/CCCD người uỷ quyền không được để trống");
        }
        if (this.authedIdNo == null || this.authedIdNo.isEmpty()) {
            throw new AuthorizationException("CC/CCCD người được uỷ quyền không được để trống");
        }
        if (this.effDate == null) {
            throw new AuthorizationException("Ngày hiệu lực không được để trống");
        }
        if (this.expiryDate == null) {
            throw new AuthorizationException("Ngày hết hạn không được để trống");
        }

        if (this.expiryDate != null &&
                this.effDate.isAfter(this.expiryDate)) {
            throw new AuthorizationException("Ngày hết hạn phải sau ngày hiệu lực");
        }
    }
    
    public void updateStatus() {
        if (this.expiryDate != null && this.expiryDate.isBefore(LocalDate.now())) {
            this.status = "DUEDATE";
        } else {
            this.status = "ACTIVE";
        }
    }
}