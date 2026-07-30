package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vix.local.api.modules.capital_source.application.service.PartnerApplicationService;
import vix.local.api.modules.capital_source.domain.model.CreditLimit;
import vix.local.api.shared.dto.ApiResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/credit-limits")
@RequiredArgsConstructor
public class CreditLimitController {

    private final PartnerApplicationService partnerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreditLimit>> createCreditLimit(
            @PathVariable UUID partnerId,
            @RequestBody CreditLimit creditLimit) {
        CreditLimit created = partnerService.createCreditLimit(partnerId, creditLimit);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CreditLimit>>> getCreditLimits(
            @PathVariable UUID partnerId) {
        List<CreditLimit> limits = partnerService.getCreditLimitsByPartnerId(partnerId);
        return ResponseEntity.ok(ApiResponse.success(limits));
    }
}