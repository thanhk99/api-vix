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
    private String mobile;
    private String email;
    private String website;

    // Loại hình khách hàng
    private String cusType;  // Phân loại KH
    private String businessType;  // Loại hình kinh tế
    private Boolean professionalInvestor;  // Nhà đầu tư chuyên nghiệp
    private LocalDate professionalStartDate;  // Ngày bắt đầu NĐT chuyên nghiệp
    private LocalDate professionalEndDate;  // Ngày kết thúc NĐT chuyên nghiệp

    private String status;
    private LocalDate lastUpdated;
}