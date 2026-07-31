package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.permission.infrastructure.security.RequireModulePermission;
import vix.local.api.modules.capital_source.application.service.PartnerApplicationService;
import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.shared.dto.ApiResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerApplicationService partnerService;

    @GetMapping
    @RequireModulePermission(module = "PARTNER_AND_CREDIT_LIMIT", permission = "R")
    public ResponseEntity<ApiResponse<List<Partner>>> getAllPartners() {
        List<Partner> partners = partnerService.getAllPartners();
        return ResponseEntity.ok(ApiResponse.success(partners));
    }

    @PostMapping
    @RequireModulePermission(module = "PARTNER_AND_CREDIT_LIMIT", permission = "C")
    public ResponseEntity<ApiResponse<Partner>> createPartner(@RequestBody Partner partner) {
        Partner created = partnerService.createPartner(partner);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping("/{id}")
    @RequireModulePermission(module = "PARTNER_AND_CREDIT_LIMIT", permission = "R")
    public ResponseEntity<ApiResponse<Partner>> getPartner(@PathVariable UUID id) {
        Partner partner = partnerService.getPartner(id);
        return ResponseEntity.ok(ApiResponse.success(partner));
    }
}