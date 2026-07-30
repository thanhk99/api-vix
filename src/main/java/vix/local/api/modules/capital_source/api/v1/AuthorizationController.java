package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vix.local.api.modules.capital_source.application.service.PartnerApplicationService;
import vix.local.api.modules.capital_source.domain.model.Authorization;
import vix.local.api.shared.dto.ApiResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/authorizations")
@RequiredArgsConstructor
public class AuthorizationController {

    private final PartnerApplicationService partnerService;

    @PostMapping
    public ResponseEntity<ApiResponse<Authorization>> createAuthorization(
            @PathVariable UUID partnerId,
            @RequestBody Authorization authorization) {
        Authorization created = partnerService.createAuthorization(partnerId, authorization);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Authorization>>> getAuthorizations(
            @PathVariable UUID partnerId) {
        List<Authorization> authorizations = partnerService.getAuthorizationsByPartnerId(partnerId);
        return ResponseEntity.ok(ApiResponse.success(authorizations));
    }
}