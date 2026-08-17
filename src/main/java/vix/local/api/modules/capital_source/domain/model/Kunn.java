package vix.local.api.modules.capital_source.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vix.local.api.modules.capital_source.domain.exception.KunnException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Kunn {

    private UUID id;
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
    private KunnStatus status; // Trạng thái
    
    private LocalDateTime createdDate;
    private UUID createUser;
    private LocalDateTime approveDate;
    private UUID approveUser;

    public void validateCreation(BigDecimal remainLimit, LocalDate limitStartDate, LocalDate limitEndDate) {
        if (lnAmt == null || lnAmt.compareTo(BigDecimal.ZERO) <= 0) {
            throw KunnException.badRequest("Số tiền giải ngân phải lớn hơn 0");
        }
        
        if (remainLimit == null || lnAmt.compareTo(remainLimit) > 0) {
            throw KunnException.badRequest("Số tiền giải ngân không được vượt quá hạn mức còn lại");
        }

        if (lnDate == null) {
            throw KunnException.badRequest("Ngày giải ngân không được để trống");
        }

        if (limitStartDate != null && lnDate.isBefore(limitStartDate)) {
            throw KunnException.badRequest("Ngày giải ngân không được trước ngày bắt đầu hạn mức");
        }

        if (limitEndDate != null && lnDate.isAfter(limitEndDate)) {
            throw KunnException.badRequest("Ngày giải ngân không được sau ngày kết thúc hạn mức");
        }

        if (actIntRate != null && contractIntRate != null && actIntRate.compareTo(contractIntRate) != 0) {
            if (reason == null || reason.trim().isEmpty()) {
                throw KunnException.badRequest("Phải nhập lý do chênh lệch khi Lãi thực tế khác Lãi HĐ");
            }
        }
        
        this.status = KunnStatus.PENDING;
        this.createdDate = LocalDateTime.now();
    }

    public void validateUpdate(BigDecimal remainLimit) {
        if (lnAmt == null || lnAmt.compareTo(BigDecimal.ZERO) <= 0) {
            throw KunnException.badRequest("Số tiền giải ngân phải lớn hơn 0");
        }
        
        if (remainLimit == null || lnAmt.compareTo(remainLimit) > 0) {
            throw KunnException.badRequest("Số tiền giải ngân mới không được vượt quá hạn mức còn lại");
        }
        
        if (actIntRate != null && contractIntRate != null && actIntRate.compareTo(contractIntRate) != 0) {
            if (reason == null || reason.trim().isEmpty()) {
                throw KunnException.badRequest("Phải nhập lý do chênh lệch khi Lãi thực tế khác Lãi HĐ");
            }
        }
    }

    public void approve(UUID approverId, BigDecimal currentRemainLimit) {
        if (this.status != KunnStatus.PENDING) {
            throw KunnException.badRequest("Chỉ được duyệt KUNN ở trạng thái PENDING");
        }

        if (lnAmt == null || lnAmt.compareTo(BigDecimal.ZERO) <= 0) {
            throw KunnException.badRequest("Số tiền giải ngân phải lớn hơn 0");
        }

        if (currentRemainLimit == null || lnAmt.compareTo(currentRemainLimit) > 0) {
            throw KunnException.badRequest("Số tiền giải ngân không được vượt quá hạn mức còn lại tại thời điểm duyệt");
        }

        this.status = KunnStatus.ACTIVE;
        this.approveUser = approverId;
        this.approveDate = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != KunnStatus.PENDING && this.status != KunnStatus.ACTIVE) {
            throw KunnException.badRequest("Chỉ được huỷ KUNN ở trạng thái PENDING hoặc ACTIVE");
        }
        this.status = KunnStatus.CANCEL;
    }
}
