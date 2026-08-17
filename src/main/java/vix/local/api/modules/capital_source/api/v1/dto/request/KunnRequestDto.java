package vix.local.api.modules.capital_source.api.v1.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class KunnRequestDto {
    private UUID cusId; // Mã đối tác
    private String contactNo; // Số HĐ tín dụng
    private UUID limitId; // Mã hạn mức
    private String lnContactNo; // Số HĐ khế ước
    private LocalDate lnContactDate; // Ngày ký khế ước
    private BigDecimal lnAmt; // Số tiền giải ngân
    private LocalDate lnDate; // Ngày giải ngân
    private BigDecimal contractIntRate; // Lãi HĐ
    private BigDecimal actIntRate; // Lãi thực tế
    private String reason; // Lý do chênh lệch
    private BigDecimal casaRate; // Tỷ lệ duy trì CASA
    private LocalDate settDate; // Ngày tất toán
    private Integer term; // Kỳ hạn
    private String currency; // Đơn vị tiền tệ
    private String purpose; // Mục đích
    private String intTerm; // Kỳ trả lãi
    private String prinTerm; // Kỳ trả gốc
}
