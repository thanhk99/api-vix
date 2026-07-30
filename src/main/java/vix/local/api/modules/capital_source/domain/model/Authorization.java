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
    private LocalDate ExpiryDate; // Ngày hết hạn
    private String authedPosition; // Chức vụ người được uỷ quyền
    private String scope; // Phạm vi UQ (có thể không có trường DB)
    private String phone; // SĐT
    private String email; // Email

    public void validateAuthorization() {
        if (this.effDate == null) {
            throw new AuthorizationException("Ngày hiệu lực không được để trống");
        }

        if (this.ExpiryDate != null &&
                this.effDate.isAfter(this.ExpiryDate)) {
            throw new AuthorizationException("Ngày hết hạn phải sau ngày hiệu lực");
        }
    }
}