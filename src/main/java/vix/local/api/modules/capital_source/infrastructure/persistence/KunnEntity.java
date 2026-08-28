package vix.local.api.modules.capital_source.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vix.local.api.modules.capital_source.domain.model.KunnStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kunn", schema = "capital_source")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KunnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "cus_id", nullable = false)
    private UUID cusId;

    @Column(name = "contact_no", length = 50, nullable = false)
    private String contactNo;

    @Column(name = "limit_id", nullable = false)
    private UUID limitId;

    @Column(name = "ln_contact_no", length = 50, nullable = false)
    private String lnContactNo;

    @Column(name = "ln_contact_date", nullable = false)
    private LocalDate lnContactDate;

    @Column(name = "ln_amt", precision = 20, scale = 2, nullable = false)
    private BigDecimal lnAmt;

    @Column(name = "ln_date", nullable = false)
    private LocalDate lnDate;

    @Column(name = "contract_int_rate", precision = 10, scale = 6, nullable = false)
    private BigDecimal contractIntRate;

    @Column(name = "act_int_rate", precision = 10, scale = 6, nullable = false)
    private BigDecimal actIntRate;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "casa_rate", precision = 10, scale = 6)
    private BigDecimal casaRate;

    @Column(name = "sett_date")
    private LocalDate settDate;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "purpose", length = 500, nullable = false)
    private String purpose;

    @Column(name = "int_term", length = 30, nullable = false)
    private String intTerm;

    @Column(name = "prin_term", length = 30, nullable = false)
    private String prinTerm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private KunnStatus status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "create_user")
    private UUID createUser;

    @Column(name = "approve_date")
    private LocalDateTime approveDate;

    @Column(name = "approve_user")
    private UUID approveUser;

    @Column(name = "prepayment_note")
    private String prepaymentNote;

    @Column(name = "note")
    private String note;

}