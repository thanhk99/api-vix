package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vix.local.api.modules.permission.infrastructure.security.RequireDeptPermission;
import vix.local.api.modules.permission.domain.model.ResourceCode;
import vix.local.api.modules.permission.domain.model.ActionCode;
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
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    public ResponseEntity<ApiResponse<List<Partner>>> getAllPartners() {
        List<Partner> partners = partnerService.getAllPartners();
        return ResponseEntity.ok(ApiResponse.success(partners));
    }

    @PostMapping
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.CREATE)
    public ResponseEntity<ApiResponse<Partner>> createPartner(@RequestBody Partner partner) {
        Partner created = partnerService.createPartner(partner);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping("/{id}")
    @RequireDeptPermission(resource = ResourceCode.CAPITAL_PARTNER, action = ActionCode.VIEW)
    public ResponseEntity<ApiResponse<Partner>> getPartner(@PathVariable UUID id) {
        Partner partner = partnerService.getPartner(id);
        return ResponseEntity.ok(ApiResponse.success(partner));
    }
}