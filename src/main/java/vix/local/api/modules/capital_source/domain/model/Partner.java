package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.PartnerException;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Partner {
    private UUID id;

    // Các trường thông tin cơ bản
    private String cusId; // Mã KH
    private String branchCusId; // Mã đơn vị GD
    private String cusName; // Tên KH
    private String shortName; // Tên viết tắt
    private String address;
    private String idCode; // Số ĐKKD/CCCD
    private LocalDate fistIssueDate; // Ngày cấp lần đầu
    private LocalDate lastIssueDate; // Ngày cấp cuối
    private String issueBy; // Nơi cấp
    private Integer changeCount; // Số lần thay đổi
    private String opLiscenseNo; // GP hoạt động
    private LocalDate opIssueDate; // Ngày cấp GP
    private String mobile;
    private String email;
    private String website;

    // Loại hình khách hàng
    private String cusType; // Phân loại KH
    private String businessType; // Loại hình kinh tế
    private Boolean professionalInvestor; // Nhà đầu tư chuyên nghiệp
    private LocalDate professionalStartDate; // Ngày bắt đầu NĐT chuyên nghiệp
    private LocalDate professionalEndDate; // Ngày kết thúc NĐT chuyên nghiệp

    // Các trường khác đã tồn tại
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDate lastUpdated;

    // Business rules methods
    public void validatePartner() {
        if (this.cusId == null || this.cusId.isEmpty()) {
            throw new PartnerException("Mã KH không được để trống");
        }

        if (this.professionalInvestor != null && this.professionalInvestor) {
            if (this.professionalStartDate == null) {
                throw new PartnerException(
                        "Ngày bắt đầu NĐT chuyên nghiệp không được để trống khi là NĐT chuyên nghiệp");
            }

            if (this.professionalEndDate != null &&
                    this.professionalStartDate.isAfter(this.professionalEndDate)) {
                throw new PartnerException("Ngày kết thúc phải sau ngày bắt đầu NĐT chuyên nghiệp");
            }
        }
    }

    public void updatePartnerInfo(String cusId, String cusName) {
        if (cusId == null || cusId.isEmpty()) {
            throw new PartnerException("Mã KH không được để trống khi cập nhật thông tin đối tác");
        }

        this.cusId = cusId;
        this.cusName = cusName;
    }
}