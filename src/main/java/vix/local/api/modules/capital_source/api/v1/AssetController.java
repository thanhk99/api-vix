package vix.local.api.modules.capital_source.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vix.local.api.modules.capital_source.application.service.PartnerApplicationService;
import vix.local.api.modules.capital_source.domain.model.Asset;
import vix.local.api.shared.dto.ApiResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/capital-source/partners/{partnerId}/assets")
@RequiredArgsConstructor
public class AssetController {

    private final PartnerApplicationService partnerService;

    @PostMapping
    public ResponseEntity<ApiResponse<Asset>> createAsset(
            @PathVariable UUID partnerId,
            @RequestBody Asset asset) {
        Asset created = partnerService.createAsset(partnerId, asset);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Asset>>> getAssets(
            @PathVariable UUID partnerId) {
        List<Asset> assets = partnerService.getAssetsByPartnerId(partnerId);
        return ResponseEntity.ok(ApiResponse.success(assets));
    }
}