package vix.local.api.modules.capital_source.api.v1.dto.response;

import lombok.Builder;
import lombok.Data;
import vix.local.api.modules.capital_source.domain.model.KunnStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class KunnResponseDto {
    private UUID id;
    private UUID cusId;
    private String cusName;
    private String cusIdCode;
    private String contactNo;
    private UUID limitId;
    private String lnContactNo;
    private LocalDate lnContactDate;
    private BigDecimal lnAmt;
    private LocalDate lnDate;
    private BigDecimal contractIntRate;
    private BigDecimal actIntRate;
    private String reason;
    private BigDecimal casaRate;
    private LocalDate settDate;
    private Integer term;
    private String currency;
    private String purpose;
    private String intTerm;
    private String prinTerm;
    private KunnStatus status;
    private LocalDateTime createdDate;
    private UUID createUser;
    private String createUserName;
    private LocalDateTime approveDate;
    private UUID approveUser;
    private String approveUserName;
    private String prepaymentNote;
    private String note;
}